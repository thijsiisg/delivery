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
import org.codehaus.jackson.JsonNode;
import org.socialhistoryservices.delivery.api.RecordLookupService;
import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.entity.Permission_;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission_;
import org.socialhistoryservices.delivery.permission.service.PermissionMailer;
import org.socialhistoryservices.delivery.permission.service.PermissionService;
import org.socialhistoryservices.delivery.record.entity.*;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.ErrorHandlingController;
import org.socialhistoryservices.delivery.InvalidRequestException;
import org.socialhistoryservices.delivery.ResourceNotFoundException;
import org.socialhistoryservices.delivery.request.controller.AbstractRequestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
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
import java.text.DateFormat;
import java.text.ParseException;
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
    protected Validator validator;

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
     * Fetches one specific permission in JSON format.
     * @param id ID of the permission to fetch.
     * @param callback The optional JSONP callback function name.
     * @param model Passed view model.
     * @param req The request.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.GET)
    @Secured("ROLE_PERMISSION_VIEW")
    public String getSingle(@PathVariable int id,
                            @RequestParam(required=false) String callback,
                            Model model, HttpServletRequest req) {

        Permission pm = permissions.getPermissionById(id);
        if (pm == null) {
           throw new ResourceNotFoundException();
        }
        model.addAttribute("callback", callback);
        model.addAttribute("permission", pm);
        return "permission_get";
    }

    /**
     * Get a list of permissions.
     * @param req The HTTP request object.
     * @param callback The optional JSONP callback function name.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/",
                    method = RequestMethod.GET)
    @Secured("ROLE_PERMISSION_VIEW")
    public String get(HttpServletRequest req,
                      @RequestParam(required=false) String callback,
                            Model model) {

        Map<String, String[]> p = req.getParameterMap();
        DateFormat apiDf = new SimpleDateFormat("yyyy-MM-dd");
        CriteriaBuilder cb = permissions.getPermissionCriteriaBuilder();
        CriteriaQuery<RecordPermission> cq = cb.createQuery(RecordPermission.class);
        Root<RecordPermission> rpRoot = cq.from(RecordPermission.class);
        cq.select(rpRoot);

        Join<RecordPermission,Permission> pmRoot = rpRoot.join(RecordPermission_.permission);

        // Expression to be the where clause of the query
        Expression where = null;

        // Filters
        where = addDateFilter(p, cb, rpRoot, where);
        where = addNameFilter(p, cb, pmRoot, where);
        where = addEmailFilter(p, cb, pmRoot, where);
        where = addResearchOrganizationFilter(p, cb, pmRoot, where);
        where = addResearchSubjectFilter(p, cb, pmRoot, where);
        where = addAddressFilter(p, cb, pmRoot, where);
        where = addExplanationFilter(p, cb, pmRoot, where);
        where = addPermissionFilter(p, cb, rpRoot, where);
        where = addSearchFilter(p, cb, rpRoot, pmRoot, where);

        // Set the where clause
        if (where != null) {
            cq.where(where);
        }

        // Set sort order and sort column
        cq.orderBy(parseOrderFilter(p, cb, rpRoot, pmRoot));

        // Fetch result set
        List<RecordPermission> rList = permissions.listPermissions(cq);
        PagedListHolder<RecordPermission> pagedListHolder = new PagedListHolder<RecordPermission>(rList);

        // Set the amount of permissions per page
        pagedListHolder.setPageSize(parsePageLenFilter(p));

        // Set the current page, internal starts at 0, external at 1
        pagedListHolder.setPage(parsePageFilter(p));

        // Add result to model
        model.addAttribute("callback", callback);
        model.addAttribute("pageListHolder", pagedListHolder);

        Calendar cal = GregorianCalendar.getInstance();
        model.addAttribute("today", cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        model.addAttribute("tomorrow",cal.getTime());

        return "permission_get_list";
    }

    /**
     * Parse the sort and sort_dir filters into an Order to be used in a query.
     * @param p The parameter list to search the filter values in.
     * @param cb The criteria builder used to construct the Order.
     * @param rpRoot The root of the record permission used to construct the Order.
     * @param pmRoot The root of the permission used to construct the Order.
     * @return The order the query should be in (asc/desc) sorted on provided
     * column. Defaults to asc on the PK column.
     */
    private Order parseOrderFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<RecordPermission> rpRoot,
                                   Join<RecordPermission,Permission> pmRoot) {
        boolean containsSort = p.containsKey("sort");
        boolean containsSortDir = p.containsKey("sort_dir");
        Expression e = pmRoot.get(Permission_.id);
        if (containsSort) {
            String sort = p.get("sort")[0];
            if (sort.equals("visitor_name")) {
                e = pmRoot.get(Permission_.name);
            } else if (sort.equals("visitor_email")) {
                e = pmRoot.get(Permission_.email);
            } else if (sort.equals("date_granted")) {
                e = rpRoot.get(RecordPermission_.dateGranted);
            } else if (sort.equals("address")) {
                e = pmRoot.get(Permission_.address);
            } else if (sort.equals("research_organization")) {
                e = pmRoot.get(Permission_.researchOrganization);
            } else if (sort.equals("research_subject")) {
                e = pmRoot.get(Permission_.researchSubject);
            } else if (sort.equals("explanation")) {
                e = pmRoot.get(Permission_.explanation);
            } else if (sort.equals("permission")) {
                e = rpRoot.get(RecordPermission_.permission);
            }
        }
        if (containsSortDir && p.get("sort_dir")[0].toLowerCase().equals("desc")) {
             return cb.desc(e);
        }
        return cb.asc(e);
    }

    /**
     * Add the search filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param rpRoot The record permission root.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addSearchFilter(Map<String, String[]> p, CriteriaBuilder cb,  Root<RecordPermission> rpRoot,
                                       Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("search") && !p.get("search")[0].trim().equals("")) {
            String search = p.get("search")[0].trim().toLowerCase();
            Join<RecordPermission, Record> record = rpRoot.join(RecordPermission_.record);
            Join<Record,ExternalRecordInfo> eRoot = record.join(Record_.externalInfo);
            Expression exSearch = cb.or(
                    cb.like(cb.lower(eRoot.get(ExternalRecordInfo_.title)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.name)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.email)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.explanation)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.researchOrganization)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.researchSubject)),
                            "%" + search + "%"),
                    cb.like(cb.lower(pmRoot.<String>get(Permission_.address)),
                            "%" + search + "%")
                    );
            where = where != null ? cb.and(where, exSearch) : exSearch;
        }
        return where;
    }

    /**
     * Add the permission filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param rpRoot The record permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addPermissionFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<RecordPermission> rpRoot,
                                           Expression where) {
        if (p.containsKey("permission")) {
            Expression exPermission = null;
            String permission = p.get("permission")[0].trim().toUpperCase();

            if (permission.equals("TRUE"))
                exPermission = cb.equal(rpRoot.<Boolean>get(RecordPermission_.granted), true);

            if (permission.equals("FALSE"))
                exPermission = cb.equal(rpRoot.<Boolean>get(RecordPermission_.granted), false);

            if (permission.equals("NULL"))
                exPermission = cb.isNull(rpRoot.<Date>get(RecordPermission_.dateGranted));

            if (exPermission != null)
                where = where != null ? cb.and(where, exPermission) : exPermission;
        }
        return where;
    }

    /**
     * Add the explanation filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addExplanationFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                            Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("explanation")) {
            Expression exExplanation = cb.like(
                     pmRoot.<String>get(Permission_.explanation),
                    "%" + p.get("explanation")[0].trim() + "%");
            where = where != null ? cb.and(where, exExplanation) : exExplanation;
        }
        return where;
    }

    /**
     * Add the address filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addAddressFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                        Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("address")) {
            Expression exAddress = cb.like(
                     pmRoot.<String>get(Permission_.address),
                    "%" + p.get("address")[0].trim() + "%");
            where = where != null ? cb.and(where, exAddress) : exAddress;
        }
        return where;
    }

    /**
     * Add the research subject filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addResearchSubjectFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("research_subject")) {
            Expression exResearch = cb.like(
                     pmRoot.<String>get(Permission_.researchSubject),
                    "%" + p.get("research_subject")[0].trim() + "%");
            where = where != null ? cb.and(where, exResearch) : exResearch;
        }
        return where;
    }

    /**
     * Add the research organization filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addResearchOrganizationFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                     Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("research_organization")) {
            Expression exResearchOrg = cb.like(
                    pmRoot.<String>get(Permission_.researchOrganization),
                    "%" + p.get("research_organization")[0].trim() + "%");
            where = where != null ? cb.and(where, exResearchOrg) : exResearchOrg;
        }
        return where;
    }

    /**
     * Add the email filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addEmailFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                      Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("visitor_email")) {
            Expression exEmail = cb.like(
                    pmRoot.<String>get(Permission_.email),
                    "%" + p.get("visitor_email")[0].trim() + "%");
            where = where != null ? cb.and(where, exEmail) : exEmail;
        }
        return where;
    }

    /**
     * Add the name filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addNameFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                     Join<RecordPermission,Permission> pmRoot, Expression where) {
        if (p.containsKey("visitor_name")) {
            Expression exName = cb.like(pmRoot.<String>get(Permission_.name),
                    "%" + p.get("visitor_name")[0].trim() + "%");
            where = where != null ? cb.and(where, exName) : exName;
        }
        return where;
    }

    /**
     * Add the date granted filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param pmRoot The permission root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addDateFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                     Root<RecordPermission> rpRoot, Expression where) {
        Date date = getDateFilter(p);
        if (date != null) {
            Expression<Boolean> exDate = cb.equal(rpRoot.<Date>get(RecordPermission_.dateGranted), date);
            where = where != null ? cb.and(where, exDate) : exDate;
        }
        else {
            Date fromDate = getFromDateFilter(p);
            Date toDate = getToDateFilter(p);
            if (fromDate != null) {
                Expression<Boolean> exDate = cb.greaterThanOrEqualTo
                    (rpRoot.<Date>get(RecordPermission_.dateGranted), fromDate);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
            if (toDate != null) {
                Expression<Boolean> exDate = cb.lessThanOrEqualTo(
                    rpRoot.<Date>get(RecordPermission_.dateGranted), toDate);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
        }
        return where;
    }

    // }}}
    // {{{ Create API

    /**
     * Create a new permission request.
     * @param json The json request body to use as parameters.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/",
                    method = RequestMethod.POST)
    public String apiCreate(@RequestBody String json) {

        // Parse the Json
        JsonNode root = parseJSONBody(json);

        // Create the form
        PermissionForm form = new PermissionForm();
        form.fillFrom(root, df);

        // Validate the form
        BindingResult res = new BeanPropertyBindingResult(form, "permission");
        validator.validate(form, res);
        if (res.hasErrors()) {
            throw InvalidRequestException.create(res);
        }

        // Create the permission
        Permission obj = new Permission();
        form.fillInto(obj, df);

        // Add the correct record permissions.
        JsonNode recordPermissionNode = root.path("items");
        int recordPermissionNum = recordPermissionNode.size();

        String[] pids = new String[recordPermissionNum];
        for (int i = 0; i < recordPermissionNum; ++i) {
            pids[i] = recordPermissionNode.path(i).getTextValue();
        }

        // Fetch the records.
        List<Record> recs = getRestrictedRecordsFromPids(pids);

        // Display a message if some items are closed
        if (recs == null || recs.isEmpty()) {
            throw new InvalidRequestException("Not all items are restricted " +
                    "or you did not specify any.");
        }

        // Create the record permission objects from the records and add them
        // to the permission.
        addRecordsToPermission(obj, recs);

        // Guarantee a unique token
        guaranteeUniqueCode(obj);
        permissions.addPermission(obj);

        return "redirect:/permission/"+obj.getId();
    }

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
        validator.validate(form, res);
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
     * Edit a permission by providing json instead of a form.
     * @param id The id of the permission to edit.
     * @param json The json request body to use as parameters.
     */
    private void editPermissionJson(int id, String json) {
        // Parse the Json
        JsonNode root = parseJSONBody(json);

        // Create the form
        PermissionForm form = new PermissionForm();
        form.fillFrom(root, df);

        // Read all (pid, granted [, motivation]) tuples into maps.
        Map<String, Boolean> g = new HashMap<String, Boolean>();
        Map<String, String> m = new HashMap<String, String>();
        Iterator<JsonNode> it = root.path("items").getElements();
        while (it.hasNext()) {
            JsonNode n = it.next();
            JsonNode pid = n.path(0);
            JsonNode granted = n.path(1);
            JsonNode motivation = n.path(2);
            if (pid.isMissingNode() || granted.isMissingNode()) {
                throw new InvalidRequestException("invalid (pid," +
                        "granted) tuple list.");
            }
            g.put(pid.getTextValue(), granted.getBooleanValue());

            if (!motivation.isMissingNode()) {
                m.put(pid.getTextValue(), motivation.getTextValue());
            }
        }

        editPermission(id, form, g, m);
    }

    /**
     * Create/update a permission (Method PUT).
     * @param json The json to use as parameters.
     * @param id The id of the permission to update.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.PUT)
    @ResponseBody
    @Secured("ROLE_PERMISSION_MODIFY")
    public String apiEdit(@RequestBody String json,
                            @PathVariable int id,
                            HttpServletRequest req) {
        editPermissionJson(id, json);
        return "";
    }

    /**
     * Create/update a permission (Method POST, !PUT in path).
     * @param json The json to use as parameters.
     * @param id The id of the permission to update.
     * @param req The request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}!PUT",
                    method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_PERMISSION_MODIFY")
    public String apiFakeEdit(@RequestBody String json,
                            @PathVariable int id,
                            HttpServletRequest req) {
        editPermissionJson(id, json);
        return "";
    }

    /**
     * Updates a list of record permissions (rp.id -> true/false)
     * @param pm The permission to update.
     * @param p The parameter map in which (recordpermission.id,
     * granted) tuples are stored.
     */
     private void updateRecordPermissions(Permission pm, Map<String,String[]> p) {
        Set<Record> parents = new HashSet<>();
        Set<RecordPermission> toRemove = new HashSet<>();

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
                Record parent = rp.getRecord().getParent();
                if (parent != null) {
                    parents.add(parent);
                    rp.setRecord(parent);
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
        for (RecordPermission rp : pm.getRecordPermissions()) {
            Record parent = rp.getRecord().getParent();
            if ((parent != null) && parents.contains(parent)) {
                toRemove.add(rp);
                permissions.removeRecordPermission(rp);
            }
        }
        pm.getRecordPermissions().removeAll(toRemove);

        // Save all changes (could also save per record permission instead).
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
    @Secured("ROLE_PERMISSION_MODIFY")
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
    @Secured("ROLE_PERMISSION_MODIFY")
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
    @Secured("ROLE_PERMISSION_DELETE")
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
    @Secured("ROLE_PERMISSION_DELETE")
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
    @Secured("ROLE_PERMISSION_DELETE")
    public String apiFakeDelete(@PathVariable int id, HttpServletRequest req) {
        remove(id);
        return "";
    }
    // }}}
}
