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

package org.socialhistoryservices.delivery.reservation.controller;

import org.codehaus.jackson.JsonNode;
import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.service.PermissionService;
import org.socialhistoryservices.delivery.record.entity.*;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.request.controller.AbstractRequestController;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.InUseException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation_;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation_;
import org.socialhistoryservices.delivery.reservation.service.*;
import org.socialhistoryservices.delivery.InvalidRequestException;
import org.socialhistoryservices.delivery.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller of the Reservation package, handles all /reservation/* requests.
 */
@Controller
@Transactional
@RequestMapping(value = "/reservation")
public class ReservationController extends AbstractRequestController {

    @Autowired
    private ReservationService reservations;

    @Autowired
    private PermissionService permissions;

    @Autowired
    private ReservationMailer resMailer;

    @Autowired
    private RecordService records;

    // {{{ Get API

    /**
     * Fetches one specific reservation in JSON format.
     *
     * @param id ID of the reservation to fetch.
     * @param callback The optional JSONP callback function name.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.GET)
    @Secured("ROLE_RESERVATION_VIEW")
    public String getSingle(@PathVariable int id,
                            @RequestParam(required = false) String callback,
                            Model model) {
        Reservation r = reservations.getReservationById(id);
        if (r == null) {
           throw new ResourceNotFoundException();
        }
        model.addAttribute("callback", callback);
        model.addAttribute("reservation", r);
        return "reservation_get";
    }

    /**
     * Get a list of reservations
     * @param req The HTTP request object.
     * @param callback The optional JSONP callback function name.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/",
                    method = RequestMethod.GET)
    @Secured("ROLE_RESERVATION_VIEW")
    public String get(HttpServletRequest req,
                      @RequestParam(required=false) String callback,
                            Model model) {
        Map<String, String[]> p = req.getParameterMap();

	    CriteriaBuilder cb = reservations.getHoldingReservationCriteriaBuilder();
	    CriteriaQuery<HoldingReservation> cq = cb.createQuery(HoldingReservation.class);
	    Root<HoldingReservation> hrRoot = cq.from(HoldingReservation.class);
	    cq.select(hrRoot);

	    Join<HoldingReservation,Reservation> resRoot = hrRoot.join
			    (HoldingReservation_.reservation);

        // Expression to be the where clause of the query
        Expression<Boolean> where = null;

        // Filters
        where = addDateFilter(p, cb, resRoot, where);
        where = addNameFilter(p, cb, resRoot, where);
        where = addEmailFilter(p, cb, resRoot, where);
        where = addStatusFilter(p, cb, resRoot, where);
        where = addSpecialFilter(p, cb, resRoot, where);
        where = addPrintedFilter(p, cb, resRoot, where);
        where = addSearchFilter(p, cb, hrRoot, resRoot, where);

        // Set the where clause
        if (where != null) {
            cq.where(where);
        }

        Join<HoldingReservation,Holding> hRoot = hrRoot.join
		        (HoldingReservation_.holding);

	    cq.orderBy(parseSortFilter(p, cb, resRoot, hRoot));

        // Fetch result set
        List<HoldingReservation> hList = reservations.listHoldingReservations(cq);
        PagedListHolder<HoldingReservation> pagedListHolder = new
                PagedListHolder<HoldingReservation>(hList);
        initOverviewModel(p, model, pagedListHolder);

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, Integer.parseInt(properties.getProperty
                ("prop_requestMaxDaysInAdvance")));
        model.addAttribute("maxReserveDate",cal.getTime());

        return "reservation_get_list";
    }

    /**
     * Parse the sort and sort_dir filters into an Order to be used in a query.
     * @param p The parameter list to search the filter values in.
     * @param cb The criteria builder used to construct the Order.
     * @param resRoot The root of the reservation used to construct the Order.
     * @return The order the query should be in (asc/desc) sorted on provided
     * column. Defaults to asc on the PK column.
     */
    private Order parseSortFilter(Map<String, String[]> p, CriteriaBuilder cb, Join<HoldingReservation,Reservation> resRoot, Join<HoldingReservation,Holding> hRoot) {
        boolean containsSort = p.containsKey("sort");
        boolean containsSortDir = p.containsKey("sort_dir");
        Expression e = resRoot.get(Reservation_.date);
        if (containsSort) {
            String sort = p.get("sort")[0];
            if (sort.equals("visitorName")) {
                e = resRoot.get(Reservation_.visitorName);
            } else if (sort.equals("visitorEmail")) {
                e = resRoot.get(Reservation_.visitorEmail);
            } else if (sort.equals("status")) {
                e = resRoot.get(Reservation_.status);
            } else if (sort.equals("printed")) {
                e = resRoot.get(Reservation_.printed);
            } else if (sort.equals("special")) {
                e = resRoot.get(Reservation_.special);
	        } else if (sort.equals("signature")) {
		        e = hRoot.get(Holding_.signature);
            } else if (sort.equals("holdingStatus")) {
	            e = hRoot.get(Holding_.status);
            }
        }
        if (containsSortDir &&
                p.get("sort_dir")[0].toLowerCase().equals("asc")) {
            return cb.asc(e);
        }
        return cb.desc(e);
    }

    /**
     * Add the search filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addSearchFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<HoldingReservation> hrRoot, Join<HoldingReservation,Reservation> resRoot, Expression<Boolean> where) {
        if (p.containsKey("search") && !p.get("search")[0].trim().equals("")) {
            String search = p.get("search")[0].trim().toLowerCase();

	        Join<HoldingReservation,Holding> hRoot = hrRoot.join(HoldingReservation_.holding);
            Join<Holding,Record> rRoot = hRoot.join(Holding_.record);
            Join<Record,ExternalRecordInfo> eRoot = rRoot.join(Record_.externalInfo);
            Expression<Boolean> exSearch = cb.or(
                    cb.like(cb.lower(eRoot.get(ExternalRecordInfo_.title)),
                            "%" + search + "%"),
                    cb.like(cb.lower(resRoot.<String>get(Reservation_
                            .visitorName)),
                            "%" + search + "%"),
                    cb.like(cb.lower(resRoot.<String>get(Reservation_
                            .visitorEmail)),
                            "%" + search + "%"),
		            cb.like(cb.lower(hRoot.<String>get(Holding_ .signature)),
				            "%" + search + "%")
                    );
            where = where != null ? cb.and(where, exSearch) : exSearch;
        }
        return where;
    }

    /**
     * Add the special filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addSpecialFilter(Map<String, String[]> p, CriteriaBuilder cb, Join<HoldingReservation,Reservation> resRoot, Expression<Boolean> where) {
        if (p.containsKey("special")) {
            Expression<Boolean> exSpecial = cb.equal(
                    resRoot.<Boolean>get(Reservation_.special),
                    Boolean.parseBoolean(p.get("special")[0]));
            where = where != null ? cb.and(where, exSpecial) : exSpecial;
        }
        return where;
    }

    /**
     * Add the printed filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addPrintedFilter(Map<String, String[]> p,
                                  CriteriaBuilder cb, Join<HoldingReservation,Reservation> resRoot, Expression<Boolean> where) {
        if (p.containsKey("printed")) {
            String printed = p.get("printed")[0].trim().toLowerCase();
            if (printed.isEmpty()) {
                return where;
            }
            Expression<Boolean> exPrinted = cb.equal(
                    resRoot.<Boolean>get(Reservation_.printed),
                    Boolean.parseBoolean(p.get("printed")[0]));
            where = where != null ? cb.and(where, exPrinted) : exPrinted;
        }
        return where;
    }

    /**
     * Add the status filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addStatusFilter(Map<String, String[]> p, CriteriaBuilder cb, Join<HoldingReservation,Reservation> resRoot, Expression<Boolean> where) {
        if (p.containsKey("status")) {
            String status = p.get("status")[0].trim().toUpperCase();
            // Tolerant to empty status to ensure the filter in
            // reservation_get_list.html.ftl works
            if (!status.equals("")) {
                try {
                    Expression<Boolean> exStatus = cb.equal(
                        resRoot.<Reservation.Status>get(Reservation_.status),
                        Reservation.Status.valueOf(status));
                    where = where != null ? cb.and(where, exStatus) : exStatus;
                } catch (IllegalArgumentException ex) {
                    throw new InvalidRequestException("No such status: " +
                            status);
                }
            }
        }
        return where;
    }

    /**
     * Add the email filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addEmailFilter(Map<String, String[]> p, CriteriaBuilder cb, Join<HoldingReservation,Reservation> resRoot, Expression<Boolean> where) {
        if (p.containsKey("visitorEmail")) {
            Expression<Boolean> exEmail = cb.like(
                     resRoot.<String>get(Reservation_.visitorEmail),
                    "%" + p.get("visitorEmail")[0].trim() + "%");
            where = where != null ? cb.and(where, exEmail) : exEmail;
        }
        return where;
    }

    /**
     * Add the name filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addNameFilter(Map<String, String[]> p,
                                     CriteriaBuilder cb,
                                     Join<HoldingReservation,Reservation> resRoot,
                                     Expression<Boolean> where) {
        if (p.containsKey("visitorName")) {
            Expression<Boolean> exName = cb.like(resRoot.<String>get
                    (Reservation_
                    .visitorName),
                    "%" + p.get("visitorName")[0].trim() + "%");
            where = where != null ? cb.and(where, exName) : exName;
        }
        return where;
    }

    /**
     * Add the date/from_date/to_date filter to the where clause, if present.
     * @param p The parameter list to search the given filter value in.
     * @param cb The criteria builder.
     * @param resRoot The reservation root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addDateFilter(Map<String, String[]> p,
                                     CriteriaBuilder cb,
                                     Join<HoldingReservation,Reservation> resRoot,
                                     Expression<Boolean> where) {
        Date date = getDateFilter(p);
        if (date != null) {
            Expression<Boolean> exDate = cb.equal(resRoot.<Date>get(Reservation_.date), date);
            where = where != null ? cb.and(where, exDate) : exDate;
        } else {
            Date fromDate = getFromDateFilter(p);
            Date toDate = getToDateFilter(p);
            if (fromDate != null) {
                Expression<Boolean> exDate = cb.greaterThanOrEqualTo(resRoot.<Date>get(Reservation_.date), date);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
            if (toDate != null) {
                Expression<Boolean> exDate = cb.lessThanOrEqualTo(resRoot.<Date>get(Reservation_.date), date);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
        }
        return where;
    }

    //}}}
    // {{{ Create/Edit API

    /**
     * Create/update a reservation (Method PUT).
     * @param newRes The new reservation.
     * @param json The json to use as parameters.
     * @param id The id of the reservation to update.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.PUT)
    @ResponseBody
    @Secured({"ROLE_RESERVATION_MODIFY", "ROLE_RESERVATION_CREATE"})
    public String apiPut(@RequestBody Reservation newRes,
                              @RequestBody String json,
                              @PathVariable int id) {
        jsonCreateOrEdit(newRes, reservations.getReservationById(id), json);
        return "";
    }

    /**
     * Create/update a reservation (Method POST, !PUT in path).
     * @param newRes The new reservation.
     * @param json The json to use as parameters.
     * @param id The id of the reservation to update.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}!PUT",
                    method = RequestMethod.POST)
    @ResponseBody
    @Secured({"ROLE_RESERVATION_MODIFY", "ROLE_RESERVATION_CREATE"})
    public String apiFakePut(@RequestBody Reservation newRes,
                              @RequestBody String json,
                              @PathVariable int id) {
        jsonCreateOrEdit(newRes, reservations.getReservationById(id), json);
        return "";
    }

    /**
     * Create/update a reservation (Method POST, !PUT in path).
     * @param newRes The new reservation.
     * @param json The json to use as parameters.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/",
                    method = RequestMethod.POST)
    @Secured("ROLE_RESERVATION_CREATE")
    public String apiPost(@RequestBody Reservation newRes,
                              @RequestBody String json) {
        jsonCreateOrEdit(newRes, null, json);
        return "redirect:/reservation/"+newRes.getId();
    }

    private void jsonCreateOrEdit(Reservation newRes, Reservation oldRes,
                          String json) {
        JsonNode n = parseJSONBody(json);
        if (oldRes != null) {
            // Make sure there is a distinction between missing nodes and
            // nodes that are explicitly set to NULL for optional fields.
            checkForMissingReservationFields(newRes, oldRes, n);
        }
        if (!n.path("items").isMissingNode()) {
            jsonItemsToHoldings(newRes, n.path("items"));
        }
        try {
            BindingResult result = new BeanPropertyBindingResult(newRes,
                "reservation");
            reservations.createOrEdit(newRes, oldRes, result);
            if (result.hasErrors()) {
                throw InvalidRequestException.create(result);
            }
        } catch (NoHoldingsException e) {
            throw new InvalidRequestException(e.getMessage());
        } catch (ClosedException e) {
            throw new InvalidRequestException(e.getMessage());
        } catch (InUseException e) {
            throw new InvalidRequestException(e.getMessage());
        }

    }

    private void checkForMissingReservationFields(Reservation newRes,
                                                  Reservation oldRes,
                                                  JsonNode n) {

        if (n.path("visitorName").isMissingNode()) {
            newRes.setVisitorName(oldRes.getVisitorName());
        }
        if (n.path("visitorEmail").isMissingNode()) {
            newRes.setVisitorEmail(oldRes.getVisitorEmail());
        }
        if (n.path("date").isMissingNode()) {
            newRes.setDate(oldRes.getDate());
        }
        if (n.path("items").isMissingNode()) {
            newRes.setHoldingReservations(oldRes.getHoldingReservations());
        }
        if (n.path("printed").isMissingNode()) {
            newRes.setPrinted(oldRes.isPrinted());
        }
        if (n.path("special").isMissingNode()) {
            newRes.setSpecial(oldRes.getSpecial());
        }
        if (n.path("status").isMissingNode()) {
            newRes.setStatus(oldRes.getStatus());
        }
    }

    private void jsonItemsToHoldings(Reservation newRes, JsonNode n) {
        List<HoldingReservation> hrs = new ArrayList<HoldingReservation>();
        Iterator<String> it = n.getFieldNames();
        while (it.hasNext()) {
            String pid = it.next();
            Record r = records.getRecordByPid(pid);
            if (r == null) {
                throw new InvalidRequestException("Items list contains " +
                        "invalid PID:" + pid);
            }
            if (n.path(pid).size() == 0) {
                Holding ah = records.getHoldingForRecord(r, true);
                if (ah == null) {
                    throw new InvalidRequestException("Items list contains " +
                            "PID with empty list, but no arbitrary holding is" +
                            " available.");
                }
                HoldingReservation hr = new HoldingReservation();
                hr.setHolding(ah);
                hrs.add(hr);
                continue;
            }
            Iterator<JsonNode> it2 = n.path(pid).iterator();
            while(it2.hasNext()) {
                String signature = it2.next().getTextValue();
                boolean has = false;
                for (Holding h : r.getHoldings()) {
                    if (h.getSignature().equals(signature)) {
                        has = true;
                        HoldingReservation hr = new HoldingReservation();
                        hr.setHolding(h);
                        hrs.add(hr);
                    }
                }
                if (!has) {
                    throw new InvalidRequestException("Item list contains " +
                            "invalid (pid,signature): (" + pid + "," +
                            "" + signature + ")" );
                }
            }
        }
        newRes.setHoldingReservations(hrs);
    }

    // }}}
    // {{{ Create Form

    /**
     * Show the create form of a reservation (visitors create form).
     * @param req The HTTP request.
     * @param path The pid/signature string (URL encoded).
     * @param code The permission code to use for restricted records.
     * @param model The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{path:.*}",
                    method = RequestMethod.GET)
    public String showCreateForm(HttpServletRequest req, @PathVariable String path,
                           @RequestParam(required=false) String code,
                           Model model) {
        Reservation newRes = new Reservation();
        newRes.setDate(reservations.getFirstValidReservationDate(new Date()));

        newRes.setHoldingReservations(uriPathToHoldingReservations(path));
        return processVisitorReservationCreation(req, newRes, null, code,
                model, false);
    }

    /**
     * Process the create form of a reservation (visitors create form).
     * @param req The HTTP request.
     * @param newRes The submitted reservation.
     * @param result The binding result to put errors in.
     * @param code The permission code to use for restricted records.
     * @param model The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{path:.*}",
                    method = RequestMethod.POST)
    public String processCreateForm(HttpServletRequest req, @ModelAttribute("reservation")
                                        Reservation newRes,
                                        BindingResult result,
                           @RequestParam(required=false) String code,
                           Model model) {
       return processVisitorReservationCreation(req, newRes, result, code,
                model, true);
    }

    private String processVisitorReservationCreation(HttpServletRequest req, Reservation newRes,
                                                     BindingResult result,
                                                     String permission,
                                                     Model model, boolean commit) {
        if (!checkHoldings(model, newRes)) return "reservation_error";


        Permission perm = permissions.getPermissionByCode(permission);
        if (!checkPermissions(model, perm, newRes)) {
            model.addAttribute("holdingReservations", newRes.getHoldingReservations());
            return "reservation_choice";
        }

        if (perm != null ) {
            if (!commit) {
                newRes.setVisitorName(perm.getName());
                newRes.setVisitorEmail(perm.getEmail());
                newRes.setDate(reservations.getFirstValidReservationDate(perm
                        .getDateFrom()));
            } else {
                newRes.setPermission(perm);
            }
            if (newRes.getDate() == null || !perm.isValidOn(newRes.getDate())
                    ) {
                model.addAttribute("error", "notValidOnDate");
                return "reservation_error";
            }
        }

        try {
            if (commit) {
                // Make sure a Captcha was entered correctly.
                checkCaptcha(req, result, model);
                reservations.createOrEdit(newRes, null, result);
                if (!result.hasErrors()) {
                    // Mail the confirmation to the visitor.
                    try {
	                    resMailer.mailConfirmation(newRes);
                    }
                    catch (MailException e) {
	                    model.addAttribute("error", "mail");
                    }
                    // Automatically print the reservation.
                    autoPrint(newRes);
                    model.addAttribute("reservation", newRes);
                    return "reservation_success";
                }
            } else {
                reservations.validateHoldings(newRes, null, true);
            }

        } catch (NoHoldingsException e) {
            throw new ResourceNotFoundException();
        } catch (InUseException e) {
            model.addAttribute("error", "availability");
            return "reservation_error";
        } catch (ClosedException e) {
            model.addAttribute("error", "restricted");
            return "reservation_error";
        }
        model.addAttribute("reservation", newRes);

        return "reservation_create";
    }

	private List<HoldingReservation> uriPathToHoldingReservations(String path) {
		List<Holding> holdings = uriPathToHoldings(path, true);
		if (holdings == null)
			return null;

		List<HoldingReservation> hrs = new ArrayList<HoldingReservation>();
		for (Holding holding : holdings) {
			HoldingReservation hr = new HoldingReservation();
			hr.setHolding(holding);
			hrs.add(hr);
		}
		return hrs;
	}

    /**
     * Print a reservation if it has been reserved between the opening and
     * closing times of the readingroom.
     * @param res The reservation to print.
     */
    private void autoPrint(final Reservation res) {
        Date create = res.getCreationDate();
        Date access = res.getDate();
        // Do not print when not reserved on same day as access.
        if (access.after(create)) {
            return;
        }

        if (isBetweenOpeningAndClosingTime(create)) {
            // Run this in a separate thread, we do nothing on failure so in
            // this case this is perfectly possible.
            // This speeds up the processing of the page for the end-user.
            new Thread(new Runnable() {

                public void run() {
                    try {
                        reservations.printReservation(res);
                    } catch (PrinterException e) {
                        // Do nothing, let an employee print it later on.
                    }
                }
            }).start();
        }
    }
    // }}}

    // {{{ Batch process reservations

    /**
     * Mass delete reservations.
     * @param req The HTTP request object.
     * @param checked The reservations marked for deletion.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess",
                    method = RequestMethod.POST,
                    params = "delete")
    @Secured("ROLE_RESERVATION_DELETE")
    public String batchProcessDelete(HttpServletRequest req,
                                     @RequestParam(required=false) Set<Integer>
                                             checked) {

        // Delete all the provided reservations
        if (checked != null) {

            for (int id : checked) {
                delete(id);
            }
        }
        String qs = req.getQueryString() != null ?
                    "?" + req.getQueryString() : "";
        return "redirect:/reservation/" + qs;
    }

    /**
     * Show print marked reservations (except already printed).
     *
     * @param req The HTTP request object.
     * @param checked The marked reservations.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess",
                    method = RequestMethod.POST,
                    params = "print")
    public String batchProcessPrint(HttpServletRequest req,
                                    @RequestParam(required = false) Set<Integer>
                                            checked) {
        String qs = req.getQueryString() != null ?
                    "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reservation/" + qs;
        }

        for(int id : checked) {
            Reservation r = reservations.getReservationById(id);
            if (r != null) {
                try {
                    reservations.printReservation(r);
                } catch (PrinterException e) {
                    return "reservation_print_failure";
                }
            }
        }

        return "redirect:/reservation/" + qs;
    }

    /**
     * Show print marked reservations (including already printed).
     *
     * @param req The HTTP request object.
     * @param checked The marked reservations.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess",
                    method = RequestMethod.POST,
                    params = "printForce")
    public String batchProcessPrintForce(HttpServletRequest req,
                                         @RequestParam(required = false) Set<Integer>
                                                 checked) {
        String qs = req.getQueryString() != null ?
                    "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reservation/" + qs;
        }

        for(int id : checked) {
            Reservation r = reservations.getReservationById(id);
            if (r != null) {
                try {
                    reservations.printReservation(r, true);
                } catch (PrinterException e) {
                    return "reservation_print_failure";
                }
            }
        }

        return "redirect:/reservation/" + qs;
    }

    /**
     * Change status of marked reservations
     * @param req The HTTP request object.
     * @param checked The reservations marked.
     * @param newStatus The status the selected reservations should be set to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess",
                    method = RequestMethod.POST,
                    params = "changeStatus")
    @Secured("ROLE_RESERVATION_MODIFY")
    public String batchProcessChangeStatus(HttpServletRequest req,
                                     @RequestParam(required=false) Set<Integer>
                                             checked,
                                     @RequestParam Reservation.Status
                                             newStatus) {
        String qs = req.getQueryString() != null ?
                    "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reservation/" + qs;
        }

        for (int id : checked) {
            Reservation r = reservations.getReservationById(id);

            // Only change reservations which exist.
            if (r == null) {
                continue;
            }

            // Set the new status and holding statuses.
            r.updateStatusAndAssociatedHoldingStatus(newStatus);
            reservations.saveReservation(r);
        }

        return "redirect:/reservation/" + qs;
    }
    // }}}
    // {{{ Delete API
    /**
     * Delete reservations.
     * @param id The identifier of the reservation to delete
     */
    private void delete(int id) {
       Reservation rs = reservations.getReservationById(id);
       if (rs != null) {
           reservations.removeReservation(rs);
       }
    }

    /**
     * Delete a reservation (Method DELETE).
     *
     * @param id The id of the reservation to delete.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}",
                    method = RequestMethod.DELETE)
    @ResponseBody
    @Secured("ROLE_RESERVATION_DELETE")
    public String apiDelete(@PathVariable int id) {
        delete(id);
        return "";
    }

    /**
     * Delete a reservation (Method POST, !DELETE in path).
     *
     * @param id The id of the reservation to delete.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id}!DELETE",
                    method = RequestMethod.POST)
    @ResponseBody
    @Secured("ROLE_RESERVATION_DELETE")
    public String apiFakeDelete(@PathVariable int id) {
        delete(id);
        return "";
    }
    // }}}
    // {{{ Model data
    /**
     * Map representation of status types of reservations for use in views.
     * @return The map {string status, enum status}.
     */
    @ModelAttribute("status_types")
    public Map<String, Reservation.Status> statusTypes() {
        Map<String, Reservation.Status> data = new HashMap<String, Reservation.Status>();
        data.put("PENDING", Reservation.Status.PENDING);
        data.put("ACTIVE", Reservation.Status.ACTIVE);
        data.put("COMPLETED", Reservation.Status.COMPLETED);
        return data;
    }
    // }}}

    // {{{ Mass Create

    /**
     * Create a reservation without restrictions of size or usage.
     * @param fromReservationId The id of a reservation to use as a base of
     * this new reservation, if applicable (not required).
     * @param model The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform",
                    method = RequestMethod.GET)
    @Secured("ROLE_RESERVATION_CREATE")
    public String showMassCreateForm(@RequestParam(required=false)
                             Integer fromReservationId, Model model) {
        Reservation newRes = new Reservation();
        if (fromReservationId != null) {
            Reservation fromReservation = reservations.getReservationById(fromReservationId);
            if (fromReservation != null) {
                newRes.setVisitorEmail(fromReservation.getVisitorEmail());
                newRes.setVisitorName(fromReservation.getVisitorName());
            }
        }
        model.addAttribute("reservation", newRes);
        return "reservation_mass_create";
    }

    /**
     * Process the search for new holdings to add to the mass reservation.
     * @param newRes The already semi-built reservation.
     * @param searchTitle The keywords to search for in the title.
     * @param searchSignature The keywords to search for in the signature.
     * @param model The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform",
                    method = RequestMethod.POST,
                    params = "searchSubmit")
    @Secured("ROLE_RESERVATION_CREATE")
    public String processSearchMassCreateForm(@ModelAttribute("reservation")
                                                  Reservation newRes,
                                              @RequestParam String searchTitle,
                                              @RequestParam String searchSignature,
                                              Model model) {
        List<Holding> holdingList = searchMassCreate(newRes, searchTitle, searchSignature);

        model.addAttribute("reservation", newRes);
        model.addAttribute("holdingList", holdingList);
        return "reservation_mass_create";
    }



    /**
     * Process the search for new holdings to add to the mass reservation.
     * @param newRes The already semi-built reservation.
     * @param result The object to save the validation errors.
     * @param searchTitle The keywords to search for in the title.
     * @param searchSignature The keywords to search for in the signature.
     * @param print Whether or not to print this reservation.
     * @param mail Whether or not to mail a reservation confirmation.
     * @param model The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform",
                    method = RequestMethod.POST)
    @Secured("ROLE_RESERVATION_CREATE")
    public String processMassCreateForm(@ModelAttribute("reservation")
                                                  Reservation newRes,
                                              BindingResult result,
                                              @RequestParam String searchTitle,
                                              @RequestParam(required=false) String searchSignature,
                                              @RequestParam(required=false)
                                              Boolean print,
                                              Boolean mail,
                                              Model model) {
        List<Holding> holdingList = searchMassCreate(newRes, searchTitle, searchSignature);

        try {

            reservations.createOrEdit(newRes, null, result);
            if (!result.hasErrors()) {
                if (print != null) {
                    reservations.printReservation(newRes);
                }
                if (mail != null) {
                    resMailer.mailConfirmation(newRes);
                }
                return "redirect:/reservation/" +newRes.getId();
            }
        } catch (InUseException e) {
                String msg =  msgSource.getMessage("reservation.error.availability", null,
                        "", LocaleContextHolder.getLocale());
                result.addError(new ObjectError(result
                        .getObjectName(), null, null, msg));
        } catch (ClosedException e) {
                String msg =  msgSource.getMessage("reservation.error" +
                        ".restricted", null,
                        "", LocaleContextHolder.getLocale());
                result.addError(new ObjectError(result
                        .getObjectName(), null, null, msg));
        } catch (NoHoldingsException e) {
            String msg =  msgSource.getMessage("reservation.error" +
                        ".noHoldings", null,
                        "", LocaleContextHolder.getLocale());
                result.addError(new ObjectError(result
                        .getObjectName(), null, null, msg));
        } catch (PrinterException e) {
            // Do nothing if printing fails.
            // You will see the printed flag being false in the overview.
        }


        model.addAttribute("reservation", newRes);
        model.addAttribute("holdingList", holdingList);
        return "reservation_mass_create";
    }
    // }}}

	/**
	 * Get a list with the number of materials reserved per day.
	 * @param req The HTTP request object.
	 * @param model Passed view model.
	 * @return The name of the view to use.
	 */
	@RequestMapping(value = "/materials", method = RequestMethod.GET)
	@Secured("ROLE_RESERVATION_VIEW")
	public String reservationMaterials(HttpServletRequest req, Model model) {
		Map<String, String[]> p = req.getParameterMap();

		Date from = new Date();
		if (p.containsKey("from_date")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				from = df.parse(p.get("from_date")[0]);
			}
			catch (ParseException ex) {
				throw new InvalidRequestException("Invalid date: " + p.get("from_date")[0]);
			}
		}

        Date to = new Date();
        if (p.containsKey("to_date")) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                to = df.parse(p.get("to_date")[0]);
            }
            catch (ParseException ex) {
                throw new InvalidRequestException("Invalid date: " + p.get("to_date")[0]);
            }
        }

		CriteriaBuilder cb = reservations.getHoldingReservationCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();

		Root<HoldingReservation> hrRoot = cq.from(HoldingReservation.class);
		Join<HoldingReservation,Reservation> resRoot = hrRoot.join
				(HoldingReservation_.reservation);
		Join<HoldingReservation,Holding> hRoot = hrRoot.join
				(HoldingReservation_.holding);
		Join<Holding,Record> rRoot = hRoot.join
				(Holding_.record);
		Join<Record,ExternalRecordInfo> eriRoot = rRoot.join
				(Record_.externalInfo);

		Expression<Date> reservationDate = resRoot.<Date>get(Reservation_.date);
		Expression<ExternalRecordInfo.MaterialType> materialType =
				eriRoot.<ExternalRecordInfo.MaterialType>get(ExternalRecordInfo_.materialType);
		Expression<Long> numberOfRequests = cb.count(materialType);

	    Expression<Boolean> fromExpr = cb.greaterThanOrEqualTo(reservationDate, from);
        Expression<Boolean> toExpor = cb.lessThanOrEqualTo(reservationDate, to);

        cq.multiselect(materialType.alias("material"), numberOfRequests.alias("noRequests"));
        cq.where(cb.and(fromExpr, toExpor));
		cq.groupBy(eriRoot.<ExternalRecordInfo.MaterialType>get(ExternalRecordInfo_.materialType));
		cq.orderBy(cb.desc(numberOfRequests));

		List<Tuple> tuples = reservations.listTuples(cq);
		model.addAttribute("tuples", tuples);

		return "reservation_materials";
	}
}
