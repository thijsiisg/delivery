/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.permission.controller;

import org.apache.log4j.Logger;
import org.socialhistoryservices.delivery.InvalidRequestException;
import org.socialhistoryservices.delivery.ResourceNotFoundException;
import org.socialhistoryservices.delivery.api.RecordLookupService;
import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.socialhistoryservices.delivery.permission.service.PermissionMailer;
import org.socialhistoryservices.delivery.permission.service.PermissionSearch;
import org.socialhistoryservices.delivery.permission.service.PermissionService;
import org.socialhistoryservices.delivery.record.entity.*;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.request.controller.AbstractRequestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller used to handle all incoming requests on /permission/*
 */
@Controller
@Transactional
@RequestMapping("/permission")
public class PermissionController extends AbstractRequestController {

    @Autowired
    protected Validator mvcValidator;

    @Autowired
    private PermissionService permissions;

    @Autowired
    private RecordService records;

    @Autowired
    private RecordLookupService lookup;

    @Autowired
    private PermissionMailer pmMailer;

    @Autowired
    private SimpleDateFormat df;

    private Logger log = Logger.getLogger(getClass());

    // {{{ Get API
    /**
     * Fetches one specific permission.
     * @param id ID of the permission to fetch.
     * @param model Passed view model.
     * @param req The request.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_PERMISSION_VIEW')")
    public String getSingle(@PathVariable int id, Model model, HttpServletRequest req) {
        Permission pm = permissions.getPermissionById(id);
        if (pm == null) {
           throw new ResourceNotFoundException();
        }
        model.addAttribute("permission", pm);
        return "permission_get";
    }

    /**
     * Get a list of permissions.
     * @param req The HTTP request object.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_PERMISSION_VIEW')")
    public String get(HttpServletRequest req, Model model) {
        Map<String, String[]> p = req.getParameterMap();
        CriteriaBuilder cb = permissions.getPermissionCriteriaBuilder();

        PermissionSearch search = new PermissionSearch(cb, p);
        CriteriaQuery<RecordPermission> cq = search.list();
        CriteriaQuery<Long> cqCount = search.count();

        // Fetch result set
        List<RecordPermission> recordPermissions = permissions.listRecordPermissions(
            cq, getFirstResult(p), getMaxResults(p));
        model.addAttribute("recordPermissions", recordPermissions);

        long recordPermissionsSize = permissions.countRecordPermissions(cqCount);
        model.addAttribute("recordPermissionsSize", recordPermissionsSize);

        initOverviewModel(model);

        return "permission_get_list";
    }

    // }}}
    // {{{ Create API

    /**
     * Guarantee a unique code to be generated for a new permission.
     * @param obj The permission to generate the code for.
     */
    private void guaranteeUniqueCode(Permission obj) {
        do {
            obj.generateCode();
        }
        while (permissions.getPermissionByCode(obj.getCode()) != null);
    }

    /**
     * Edit a permission.
     * @param id The id of the permission to edit.
     * @param form The submitted form to process.
     * @param granted The (pid,granted) tuple map.
     * @param motivations The (pid,motivation) tuple map.
     */
    private void editPermission(int id, PermissionForm form, Map<String,
            Boolean> granted, Map<String, String> motivations) {

        // Get the correct permission
        Permission obj = permissions.getPermissionById(id);

        if (obj == null) {
            throw new ResourceNotFoundException();
        }

        // Validate the form
        BindingResult res = new BeanPropertyBindingResult(form, "permission");
        mvcValidator.validate(form, res);
        if (res.hasErrors()) {
            throw InvalidRequestException.create(res);
        }

        // Update the permission
        form.fillInto(obj, df);

        // Remove the removed record permissions from the permission.
        removeRecordPermissions(granted, obj);

        // Add/Update new record permissions.
        addOrUpdateRecordPermissions(granted, motivations, obj);

        // Save the changes.
        permissions.savePermission(obj);
    }

    /**
     * Add and/or update the record permissions. Basically: obj_records =
     * UNION(granted_records, obj_records), and updating the already present
     * record permissions to the status given by the granted parameter.
     * @param granted The (pid, granted) tuple map.
     * @param motivations The (pid,motivation) tuple map.
     * @param obj The permission to edit.
     */
    private void addOrUpdateRecordPermissions(Map<String, Boolean> granted,
                                              Map<String, String> motivations,
                                              Permission obj) {
        for (Map.Entry<String, Boolean> e : granted.entrySet()) {
            Iterator<RecordPermission> it3 = obj.getRecordPermissions()
                    .iterator();
            boolean has = false;
            while (it3.hasNext()) {
                RecordPermission rp = it3.next();
                Record r = rp.getRecord();
                // Update
                if (r.getPid().equals(e.getKey())) {
                    rp.setGranted(e.getValue());
                    if (motivations.containsKey(e.getKey())) {
                        rp.setMotivation(motivations.get(e.getKey()));
                    }
                    has = true;
                }
            }

            // Add a new permission if it did not exist yet.
            if (!has) {
                RecordPermission newRp = new RecordPermission();
                Record r = records.getRecordByPid(e.getKey());
                if (r == null) {
                    throw new InvalidRequestException("Invalid PID provided " +
                            "for record to get permission on.");
                }
                newRp.setRecord(r);
                newRp.setPermission(obj);
                newRp.setGranted(e.getValue());
                if (motivations.containsKey(e.getKey())) {
                    newRp.setMotivation(motivations.get(e.getKey()));
                }
                obj.addRecordPermission(newRp);
            }
        }
    }

    /**
     * Removes all record permissions from Permission which are not in the
     * granted parameter.
     * Basically: obj_records = INTERSECT(object_records, granted_records).
     * @param granted The (pid, granted) tuple map.
     * @param obj The permission to edit.
     */
    private void removeRecordPermissions(Map<String, Boolean> granted,
                                         Permission obj) {
        Iterator<RecordPermission> it = obj.getRecordPermissions().iterator();
        while (it.hasNext()) {
            RecordPermission rp = it.next();
            if (!granted.containsKey(rp.getRecord().getPid())) {
                // Remove the record from the permission.
                it.remove();
            }

        }
    }

    /**
     * Updates a list of record permissions (rp.id -> true/false)
     * @param pm The permission to update.
     * @param p The parameter map in which (recordpermission.id,
     * granted) tuples are stored.
     */
     private void updateRecordPermissions(Permission pm, Map<String,String[]> p) {
        Map<Record, RecordPermission> parents = new HashMap<>();

        // Update all granted statuses, if found.
        for (RecordPermission rp : pm.getRecordPermissions()) {
            String gKey = "granted_" + rp.getId();
            String pKey = "parent_" + rp.getId();
            String mKey = "motivation_" + rp.getId();
            if (p.containsKey(gKey) && !p.get(gKey)[0].trim().equals("null")) {
                rp.setGranted(p.get(gKey)[0].trim().equals("true"));
                rp.setDateGranted(new Date());
            }
            if (p.containsKey(pKey)) {
                Record record = rp.getRecord();
                Record parent = record.getParent();
                if (parent != null) {
                    parents.put(parent, rp);
                    rp.setRecord(parent);
                    rp.setOriginalRequestPids(record.getPid());
                }
            }
            if (p.containsKey(mKey)) {
                String motivation = p.get(mKey)[0].trim();
                if (!motivation.isEmpty()) {
                    rp.setMotivation(motivation);
                }
            }
        }

        // Now remove all redundant record permissions.
        Set<RecordPermission> toRemove = new HashSet<>();
        for (RecordPermission rp : pm.getRecordPermissions()) {
            Record record = rp.getRecord();
            Record parent = record.getParent();
            if ((parent != null) && parents.containsKey(parent)) {
                RecordPermission parentRp = parents.get(parent);
                parentRp.setOriginalRequestPids(
                    parentRp.getOriginalRequestPids() + deliveryProperties.getPidSeperator() + record.getPid());

                toRemove.add(rp);
                permissions.removeRecordPermission(rp);
            }
        }
        pm.getRecordPermissions().removeAll(toRemove);

        // Save all changes.
        permissions.savePermission(pm);
    }

    /**
     * Save a permission with the save button in the /permission/[id] form.
     * @param id The id of the permission to save.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/process",
                    method = RequestMethod.POST,
                    params = "save")
    @PreAuthorize("hasRole('ROLE_PERMISSION_MODIFY')")
    public String formSave(@RequestParam int id, HttpServletRequest req) {
        Permission pm = permissions.getPermissionById(id);
        if (pm == null) {
           throw new InvalidRequestException("No such permission");
        }
        updateRecordPermissions(pm, req.getParameterMap());
        return "redirect:/permission/";
    }

    /**
     * Save a permission and send a message to the requester.
     * @param id The id of the permission to save.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/process",
                    method = RequestMethod.POST,
                    params = "saveandemail")
    @PreAuthorize("hasRole('ROLE_PERMISSION_MODIFY')")
    public String formSaveAndFinish(@RequestParam int id,
                                 HttpServletRequest req) {
        Permission pm = permissions.getPermissionById(id);
        if (pm == null) {
           throw new InvalidRequestException("No such permission.");
        }
        updateRecordPermissions(pm, req.getParameterMap());

        // Notify the requester.
        try {
            pmMailer.mailCode(pm);
        }
        catch (MailException e) {
            log.error("Failed to send email", e);
            throw e;
        }

        return "redirect:/permission/";
    }

    // }}}
    // {{{ Create Form
    /**
     * Handle permission request form.
     * @param req The HTTP request.
     * @param pids The records to request.
     * @param model The page's model.
     * @param form (Optional) form that was filled in.
     * @param result (Optional) result of form validation.
     * @return View name to render.
     */
    public String create(HttpServletRequest req, String[] pids, Model model, PermissionForm form, BindingResult result) {
        // Try to fetch restricted records.
        List<Record> recs = getRestrictedRecordsFromPids(pids);

        // Check restrictions
        if (recs == null || recs.isEmpty()) {
            model.addAttribute("error", "invalid");
            return "permission_error";
        }

        // Add the records to the model.
        model.addAttribute("records", recs);

        // Create new form
        if (form == null) {
            form = new PermissionForm();
        }
        else if (result != null) {
            checkCaptcha(req, result, model);

            if (!result.hasErrors()) {
                // Create the permission
                Permission obj = new Permission();
                form.fillInto(obj, df);

                // Add records
                addRecordsToPermission(obj, recs);

                // Generate a unique token
                guaranteeUniqueCode(obj);
                permissions.addPermission(obj);

                try {
                    pmMailer.mailConfirmation(obj);
                    pmMailer.mailReadingRoom(obj);
                }
                catch (MailException e) {
                    log.error("Failed to send email", e);
                    model.addAttribute("error", "mail");
                }

                return "permission_success";
            }
        }

        // Add to model
        if (result == null) {
            model.addAttribute("permission", form);
        }

        return "permission_create";
    }

    /**
     * Add restricted records to a list.
     * @param pids The list of restricted record PIDs.
     * @return The list of restricted records or null when the list of PIDs
     * contains a record which is not restricted or a non-existing record.
     */
    private List<Record> getRestrictedRecordsFromPids(String[] pids) {
        List<Record> recs = new ArrayList<Record>();
        for (String pid : pids) {
            if (pid.length() == 0) {
                continue;
            }

            Record rec = records.getRecordByPid(pid);
            if (rec == null) {
                return null;
            }
            recs.add(rec);

            if (rec.getRestriction() != ExternalRecordInfo.Restriction.RESTRICTED) {
                return null;
            }
        }
        return recs;
    }

    /**
     * Add new records to a permission, granted set to false.
     * @param obj The permission to add the records to.
     * @param recs The records to add.
     */
    private void addRecordsToPermission(Permission obj, List<Record> recs) {
        for (Record rec : recs) {
            RecordPermission p = new RecordPermission();
            p.setRecord(rec);
            p.setPermission(obj);
            obj.addRecordPermission(p);

        }
    }

    /**
     * Form to request permission for a set of records.
     * @param pids The PIDs to create a permission for.
     * @param model The model to use.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{pids:.*}",
                    method = RequestMethod.GET)
    public String createForm(HttpServletRequest req, @PathVariable String pids,
                             Model model) {
        return create(req, getPidsFromURL(pids), model, null, null);
    }

    /**
     * Submitted form to request permission.
     * @param pids The PIDs to create a permission for.
     * @param permission The permission form submitted.
     * @param result The result of validating the form.
     * @param model The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{pids:.*}",
                    method = RequestMethod.POST)
    public String createForm(HttpServletRequest req, @PathVariable String pids,
             @ModelAttribute("permission") @Valid PermissionForm permission,
             BindingResult result,
             Model model) {
        return create(req, getPidsFromURL(pids), model, permission, result);
    }
    // }}}
    // {{{ Delete API
    /**
     * Delete permissions.
     * @param id The id of the permission to remove.
     */
    private void remove(int id) {
        Permission pm = permissions.getPermissionById(id);
        if (pm != null) {
            permissions.removePermission(pm);
        }
    }

    /**
     * Remove a permission (DELETE method).
     * @param id The id of the permission to remove.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERMISSION_DELETE')")
    public String apiDelete(@PathVariable int id, HttpServletRequest req) {
        remove(id);
        return "";
    }

    /**
     * Remove a permission with the delete button in the /permission/[id] form.
     * @param id The id of the permission to remove.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/process",
                    method = RequestMethod.POST,
                    params = "delete")
    @PreAuthorize("hasRole('ROLE_PERMISSION_DELETE')")
    public String formDelete(@RequestParam int id, HttpServletRequest req) {
        remove(id);
        return "redirect:/permission/";
    }

    /**
     * Remove a permission (POST method, !DELETE in path).
     * @param id The id of the permission to remove.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}!DELETE",
                    method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERMISSION_DELETE')")
    public String apiFakeDelete(@PathVariable int id, HttpServletRequest req) {
        remove(id);
        return "";
    }
    // }}}
}
