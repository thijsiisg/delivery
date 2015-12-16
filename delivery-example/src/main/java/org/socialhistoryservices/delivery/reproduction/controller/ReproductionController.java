package org.socialhistoryservices.delivery.reproduction.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.socialhistoryservices.delivery.InvalidRequestException;
import org.socialhistoryservices.delivery.ResourceNotFoundException;
import org.socialhistoryservices.delivery.api.PayWayMessage;
import org.socialhistoryservices.delivery.api.PayWayService;
import org.socialhistoryservices.delivery.record.entity.*;
import org.socialhistoryservices.delivery.reproduction.entity.*;
import org.socialhistoryservices.delivery.reproduction.entity.Order;
import org.socialhistoryservices.delivery.reproduction.service.*;
import org.socialhistoryservices.delivery.reproduction.util.ReproductionStandardOptions;
import org.socialhistoryservices.delivery.request.controller.AbstractRequestController;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.socialhistoryservices.delivery.request.util.BulkActionIds;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.awt.print.PrinterException;
import java.beans.PropertyEditorSupport;
import java.util.*;

/**
 * Controller of the Reproducion package, handles all /reproduction/* requests.
 */
@Controller
@Transactional
@RequestMapping(value = "/reproduction")
public class ReproductionController extends AbstractRequestController {
    private static final Log LOGGER = LogFactory.getLog(ReproductionController.class);

    @Autowired
    private ReproductionService reproductions;

    @Autowired
    private ReproductionMailer reproductionMailer;

    @Autowired
    private PayWayService payWayService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);

        // This is needed for passing a reproduction standard option ID
        binder.registerCustomEditor(ReproductionStandardOption.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                int id = Integer.parseInt(text);
                ReproductionStandardOption reproductionStandardOption =
                        reproductions.getReproductionStandardOptionById(id);
                setValue(reproductionStandardOption);
            }
        });
    }

    /**
     * Map representation of status types of reproductions for use in views.
     *
     * @return The map {string status, enum status}.
     */
    @ModelAttribute("status_types")
    public Map<String, Reproduction.Status> statusTypes() {
        Map<String, Reproduction.Status> data = new LinkedHashMap<String, Reproduction.Status>();
        data.put("WAITING_FOR_ORDER_DETAILS", Reproduction.Status.WAITING_FOR_ORDER_DETAILS);
        data.put("HAS_ORDER_DETAILS", Reproduction.Status.HAS_ORDER_DETAILS);
        data.put("CONFIRMED", Reproduction.Status.CONFIRMED);
        data.put("ACTIVE", Reproduction.Status.ACTIVE);
        data.put("COMPLETED", Reproduction.Status.COMPLETED);
        data.put("DELIVERED", Reproduction.Status.DELIVERED);
        data.put("CANCELLED", Reproduction.Status.CANCELLED);
        return data;
    }

    /**
     * Map representation of SOR level for use in views.
     *
     * @return The map.
     */
    @ModelAttribute("levels")
    public Map<String, ReproductionStandardOption.Level> levels() {
        Map<String, ReproductionStandardOption.Level> data = new LinkedHashMap<String, ReproductionStandardOption.Level>();
        data.put("MASTER", ReproductionStandardOption.Level.MASTER);
        data.put("LEVEL1", ReproductionStandardOption.Level.LEVEL1);
        return data;
    }

    /**
     * Map representation of SOR level for use in views.
     *
     * @return The map.
     */
    @ModelAttribute("materialTypes")
    public Map<String, ExternalRecordInfo.MaterialType> materialTypes() {
        Map<String, ExternalRecordInfo.MaterialType> data = new LinkedHashMap<String, ExternalRecordInfo.MaterialType>();
        data.put("ARTICLE", ExternalRecordInfo.MaterialType.ARTICLE);
        data.put("SERIAL", ExternalRecordInfo.MaterialType.SERIAL);
        data.put("BOOK", ExternalRecordInfo.MaterialType.BOOK);
        data.put("SOUND", ExternalRecordInfo.MaterialType.SOUND);
        data.put("DOCUMENTATION", ExternalRecordInfo.MaterialType.DOCUMENTATION);
        data.put("ARCHIVE", ExternalRecordInfo.MaterialType.ARCHIVE);
        data.put("VISUAL", ExternalRecordInfo.MaterialType.VISUAL);
        data.put("MOVING_VISUAL", ExternalRecordInfo.MaterialType.MOVING_VISUAL);
        data.put("OTHER", ExternalRecordInfo.MaterialType.OTHER);
        return data;
    }

    /**
     * Fetches one specific reproduction.
     *
     * @param id    ID of the reproduction to fetch.
     * @param model Passed view model.
     * @param error In case of an error.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/{id:[\\d]+}", method = RequestMethod.GET)
    @Secured("ROLE_REPRODUCTION_VIEW")
    public String getSingle(@PathVariable int id, Model model, @RequestParam(required = false) String error) {
        Reproduction r = reproductions.getReproductionById(id);
        if (r == null) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("reproduction", r);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(r.getHoldings()));

        // Was there an email error?
        if (error != null)
            model.addAttribute("error", error);

        return "reproduction_get";
    }

    /**
     * Get a list of reproductions.
     *
     * @param req   The HTTP request object.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Secured("ROLE_REPRODUCTION_VIEW")
    public String get(HttpServletRequest req, Model model) {
        Map<String, String[]> p = req.getParameterMap();

        CriteriaBuilder cb = reproductions.getHoldingReproductionCriteriaBuilder();
        CriteriaQuery<HoldingReproduction> cq = cb.createQuery(HoldingReproduction.class);
        Root<HoldingReproduction> hrRoot = cq.from(HoldingReproduction.class);
        cq.select(hrRoot);

        Join<HoldingReproduction, Reproduction> rRoot = hrRoot.join(HoldingReproduction_.reproduction);

        // Expression to be the where clause of the query
        Expression<Boolean> where = null;

        // Filters
        where = addDateFilter(p, cb, rRoot, where);
        where = addNameFilter(p, cb, rRoot, where);
        where = addEmailFilter(p, cb, rRoot, where);
        where = addStatusFilter(p, cb, rRoot, where);
        where = addPrintedFilter(p, cb, hrRoot, where);
        where = addSearchFilter(p, cb, hrRoot, rRoot, where);

        // Set the where clause
        if (where != null)
            cq.where(where);

        Join<HoldingReproduction, Holding> hRoot = hrRoot.join(HoldingReproduction_.holding);

        cq.orderBy(parseSortFilter(p, cb, hrRoot, rRoot, hRoot));

        // Fetch result set
        List<HoldingReproduction> hList = reproductions.listHoldingReproductions(cq);
        PagedListHolder<HoldingReproduction> pagedListHolder = new PagedListHolder<HoldingReproduction>(hList);
        initOverviewModel(p, model, pagedListHolder);

        // Fetch holding active request information
        List<HoldingReproduction> holdingReproductions = pagedListHolder.getPageList();
        Set<Holding> holdings = getHoldings(holdingReproductions);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(holdings));

        return "reproduction_get_list";
    }

    /**
     * Add the date/from_date/to_date filter to the where clause, if present.
     *
     * @param p     The parameter list to search the given filter value in.
     * @param cb    The criteria builder.
     * @param rRoot The reproduction root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addDateFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                              Join<HoldingReproduction, Reproduction> rRoot,
                                              Expression<Boolean> where) {
        Date date = getDateFilter(p);
        if (date != null) {
            Expression<Boolean> exDate = cb.between(rRoot.<Date>get(Reproduction_.date), date, date);
            where = (where != null) ? cb.and(where, exDate) : exDate;
        }
        else {
            Date fromDate = getFromDateFilter(p);
            Date toDate = getToDateFilter(p);
            if (fromDate != null) {
                Expression<Boolean> exDate = cb.greaterThanOrEqualTo(rRoot.<Date>get(Reproduction_.date), fromDate);
                where = (where != null) ? cb.and(where, exDate) : exDate;
            }
            if (toDate != null) {
                Expression<Boolean> exDate = cb.lessThanOrEqualTo(rRoot.<Date>get(Reproduction_.date), toDate);
                where = (where != null) ? cb.and(where, exDate) : exDate;
            }
        }
        return where;
    }

    /**
     * Add the name filter to the where clause, if present.
     *
     * @param p     The parameter list to search the given filter value in.
     * @param cb    The criteria builder.
     * @param rRoot The reproduction root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addNameFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                              Join<HoldingReproduction, Reproduction> rRoot,
                                              Expression<Boolean> where) {
        if (p.containsKey("customerName")) {
            Expression<Boolean> exName = cb.like(rRoot.<String>get(Reproduction_.customerName),
                    "%" + p.get("customerName")[0].trim() + "%");
            where = (where != null) ? cb.and(where, exName) : exName;
        }
        return where;
    }

    /**
     * Add the email filter to the where clause, if present.
     *
     * @param p     The parameter list to search the given filter value in.
     * @param cb    The criteria builder.
     * @param rRoot The reproduction root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addEmailFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                               Join<HoldingReproduction, Reproduction> rRoot,
                                               Expression<Boolean> where) {
        if (p.containsKey("customerEmail")) {
            Expression<Boolean> exEmail = cb.like(rRoot.get(Reproduction_.customerEmail),
                    "%" + p.get("customerEmail")[0].trim() + "%");
            where = (where != null) ? cb.and(where, exEmail) : exEmail;
        }
        return where;
    }

    /**
     * Add the status filter to the where clause, if present.
     *
     * @param p     The parameter list to search the given filter value in.
     * @param cb    The criteria builder.
     * @param rRoot The reproduction root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addStatusFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                Join<HoldingReproduction, Reproduction> rRoot,
                                                Expression<Boolean> where) {
        if (p.containsKey("status")) {
            String status = p.get("status")[0].trim().toUpperCase();
            if (!status.equals("")) { // Tolerant to empty status
                try {
                    Expression<Boolean> exStatus = cb.equal(rRoot.<Reproduction.Status>get(Reproduction_.status),
                            Reproduction.Status.valueOf(status));
                    where = (where != null) ? cb.and(where, exStatus) : exStatus;
                } catch (IllegalArgumentException ex) {
                    throw new InvalidRequestException("No such status: " + status);
                }
            }
        }
        return where;
    }

    /**
     * Add the printed filter to the where clause, if present.
     *
     * @param p     The parameter list to search the given filter value in.
     * @param cb    The criteria builder.
     * @param hrRoot The holding reproduction root.
     * @param where The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addPrintedFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                 Root<HoldingReproduction> hrRoot,
                                                 Expression<Boolean> where) {
        if (p.containsKey("printed")) {
            String printed = p.get("printed")[0].trim().toLowerCase();
            if (printed.isEmpty()) {
                return where;
            }

            Expression<Boolean> exPrinted = cb.equal(hrRoot.<Boolean>get(HoldingReproduction_.printed),
                    Boolean.parseBoolean(p.get("printed")[0]));
            where = (where != null) ? cb.and(where, exPrinted) : exPrinted;
        }
        return where;
    }

    /**
     * Add the search filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param hrRoot The holding reproduction root.
     * @param rRoot  The reproduction root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression<Boolean> addSearchFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                Root<HoldingReproduction> hrRoot,
                                                Join<HoldingReproduction, Reproduction> rRoot,
                                                Expression<Boolean> where) {
        if (p.containsKey("search") && !p.get("search")[0].trim().equals("")) {
            String search = p.get("search")[0].trim().toLowerCase();

            Join<HoldingReproduction, Holding> hRoot = hrRoot.join(HoldingReproduction_.holding);
            Join<Holding, Record> recRoot = hRoot.join(Holding_.record);
            Join<Record, ExternalRecordInfo> eRoot = recRoot.join(Record_.externalInfo);

            Expression<Boolean> exSearch = cb.or(
                    cb.like(cb.lower(eRoot.get(ExternalRecordInfo_.title)), "%" + search + "%"),
                    cb.like(cb.lower(rRoot.<String>get(Reproduction_.customerName)), "%" + search + "%"),
                    cb.like(cb.lower(rRoot.<String>get(Reproduction_.customerEmail)), "%" + search + "%"),
                    cb.like(cb.lower(hRoot.<String>get(Holding_.signature)), "%" + search + "%")
            );

            where = (where != null) ? cb.and(where, exSearch) : exSearch;
        }
        return where;
    }

    /**
     * Parse the sort and sort_dir filters into an Order to be used in a query.
     *
     * @param p      The parameter list to search the filter values in.
     * @param cb     The criteria builder used to construct the Order.
     * @param hrRoot The root of the reproduction holding used to construct the Order.
     * @param rRoot  The root of the reproduction used to construct the Order.
     * @param hRoot  The root of the holding used to construct the Order.
     * @return The order the query should be in (asc/desc) sorted on provided column. Defaults to asc on the PK column.
     */
    private javax.persistence.criteria.Order parseSortFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                             From<?, HoldingReproduction> hrRoot,
                                                             From<?, Reproduction> rRoot,
                                                             From<?, Holding> hRoot) {
        boolean containsSort = p.containsKey("sort");
        boolean containsSortDir = p.containsKey("sort_dir");
        Expression e = rRoot.get(Reproduction_.creationDate);

        if (containsSort) {
            String sort = p.get("sort")[0];
            if (sort.equals("customerName"))
                e = rRoot.get(Reproduction_.customerName);
            else if (sort.equals("customerEmail"))
                e = rRoot.get(Reproduction_.customerEmail);
            else if (sort.equals("status"))
                e = rRoot.get(Reproduction_.status);
            else if (sort.equals("printed"))
                e = hrRoot.get(HoldingReproduction_.printed);
            else if (sort.equals("signature"))
                e = hRoot.get(Holding_.signature);
            else if (sort.equals("holdingStatus"))
                e = hRoot.get(Holding_.status);
        }

        if (containsSortDir && p.get("sort_dir")[0].toLowerCase().equals("asc"))
            return cb.asc(e);
        return cb.desc(e);
    }

    /**
     * Mass delete reproductions.
     *
     * @param req     The HTTP request object.
     * @param checked The reproductions marked for deletion.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess", method = RequestMethod.POST, params = "delete")
    @Secured("ROLE_REPRODUCTION_DELETE")
    public String batchProcessDelete(HttpServletRequest req, @RequestParam(required = false) List<String> checked) {
        // Delete all the provided reproductions
        if (checked != null) {
            for (BulkActionIds bulkActionIds : getIdsFromBulk(checked)) {
                Reproduction r = reproductions.getReproductionById(bulkActionIds.getRequestId());
                if (r != null) {
                    reproductions.removeReproduction(r);
                }
            }
        }

        String qs = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";
        return "redirect:/reproduction/" + qs;
    }

    /**
     * Show print marked holdings (except already printed).
     *
     * @param req     The HTTP request object.
     * @param checked The marked reproductions.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess", method = RequestMethod.POST, params = "print")
    public String batchProcessPrint(HttpServletRequest req, @RequestParam(required = false) List<String> checked) {
        String qs = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reproduction/" + qs;
        }

        List<HoldingReproduction> hrs = new ArrayList<HoldingReproduction>();
        for (BulkActionIds bulkActionIds : getIdsFromBulk(checked)) {
            Reproduction r = reproductions.getReproductionById(bulkActionIds.getRequestId());
            for (HoldingReproduction hr : r.getHoldingReproductions()) {
                if (hr.getHolding().getId() == bulkActionIds.getHoldingId())
                    hrs.add(hr);
            }

            if (!hrs.isEmpty()) {
                try {
                    reproductions.printItems(hrs, false);
                } catch (PrinterException e) {
                    return "reproduction_print_failure";
                }
            }
        }

        return "redirect:/reproduction/" + qs;
    }

    /**
     * Show print marked holdings (including already printed).
     *
     * @param req     The HTTP request object.
     * @param checked The marked reproductions.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess", method = RequestMethod.POST, params = "printForce")
    public String batchProcessPrintForce(HttpServletRequest req, @RequestParam(required = false) List<String> checked) {
        String qs = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reproduction/" + qs;
        }

        List<HoldingReproduction> hrs = new ArrayList<HoldingReproduction>();
        for (BulkActionIds bulkActionIds : getIdsFromBulk(checked)) {
            Reproduction r = reproductions.getReproductionById(bulkActionIds.getRequestId());
            for (HoldingReproduction hr : r.getHoldingReproductions()) {
                if (hr.getHolding().getId() == bulkActionIds.getHoldingId())
                    hrs.add(hr);
            }

            if (!hrs.isEmpty()) {
                try {
                    reproductions.printItems(hrs, true);
                } catch (PrinterException e) {
                    return "reproduction_print_failure";
                }
            }
        }

        return "redirect:/reproduction/" + qs;
    }

    /**
     * Change status of marked reproductions.
     *
     * @param req       The HTTP request object.
     * @param checked   The reproductions marked.
     * @param newStatus The status the selected reproductions should be set to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess", method = RequestMethod.POST, params = "changeStatus")
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String batchProcessChangeStatus(HttpServletRequest req, @RequestParam(required = false) List<String> checked,
                                           @RequestParam Reproduction.Status newStatus) {
        String qs = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no reservations were selected
        if (checked == null) {
            return "redirect:/reproduction/" + qs;
        }

        for (BulkActionIds bulkActionIds : getIdsFromBulk(checked)) {
            Reproduction r = reproductions.getReproductionById(bulkActionIds.getRequestId());

            // Only change reproductions which exist
            if (r != null) {
                reproductions.updateStatusAndAssociatedHoldingStatus(r, newStatus);
                reproductions.saveReproduction(r);
            }
        }

        return "redirect:/reproduction/" + qs;
    }

    /**
     * Change status of marked holdings.
     *
     * @param req              The HTTP request object.
     * @param checked          The holdings marked.
     * @param newHoldingStatus The status the selected holdings should be set to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/batchprocess", method = RequestMethod.POST, params = "changeHoldingStatus")
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String batchProcessChangeHoldingStatus(HttpServletRequest req,
                                                  @RequestParam(required = false) List<String> checked,
                                                  @RequestParam Holding.Status newHoldingStatus) {
        String qs = (req.getQueryString() != null) ? "?" + req.getQueryString() : "";

        // Simply redirect to previous page if no holdings were selected
        if (checked == null) {
            return "redirect:/reproduction/" + qs;
        }

        for (BulkActionIds bulkActionIds : getIdsFromBulk(checked)) {
            Holding h = records.getHoldingById(bulkActionIds.getHoldingId());
            if (h != null) {
                // Only update the status if the holding is active for the same reproduction
                Request request = requests.getActiveFor(h);
                if ((request instanceof Reproduction) &&
                        (((Reproduction) request).getId() == bulkActionIds.getRequestId())) {
                    // Set the new status
                    requests.updateHoldingStatus(h, newHoldingStatus);
                    records.saveHolding(h);
                }
            }
        }

        return "redirect:/reproduction/" + qs;
    }

    /**
     * Show the create form of a reproduction.
     *
     * @param req   The HTTP request.
     * @param path  The pid/signature string (URL encoded).
     * @param model The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{path:.*}", method = RequestMethod.GET)
    public String showCreateForm(HttpServletRequest req, @PathVariable String path, Model model) {
        Reproduction reproduction = new Reproduction();
        reproduction.setHoldingReproductions(uriPathToHoldingReproductions(path));
        return processReproductionCreation(req, reproduction, null, model, false);
    }

    /**
     * Process the create form of a reproduction.
     *
     * @param req    The HTTP request.
     * @param newRep The submitted reproduction.
     * @param result The binding result to put errors in.
     * @param model  The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/createform/{path:.*}", method = RequestMethod.POST)
    public String processCreateForm(HttpServletRequest req, @ModelAttribute("reproduction") Reproduction newRep,
                                    BindingResult result, Model model) {
        return processReproductionCreation(req, newRep, result, model, true);
    }

    /**
     * Translates the URI path to a list of holding reproductions.
     *
     * @param path The given path.
     * @return A list of created holding reproductions.
     */
    private List<HoldingReproduction> uriPathToHoldingReproductions(String path) {
        List<Holding> holdings = uriPathToHoldings(path, false);
        if (holdings == null)
            return null;

        List<HoldingReproduction> hrs = new ArrayList<HoldingReproduction>();
        for (Holding holding : holdings) {
            HoldingReproduction hr = new HoldingReproduction();
            hr.setHolding(holding);
            hrs.add(hr);
        }
        return hrs;
    }

    /**
     * Processes the reproduction creation procedure.
     *
     * @param req          The request.
     * @param reproduction The reproduction to create.
     * @param result       The binding result.
     * @param model        The model.
     * @param commit       Whether to commit the result to the database.
     * @return The view to resolve.
     */
    private String processReproductionCreation(HttpServletRequest req, Reproduction reproduction, BindingResult result,
                                               Model model, boolean commit) {
        if (!checkHoldings(model, reproduction))
            return "reproduction_error";

        // Add all the standard reproduction options and custom notes to the model
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                getStandardReproductionOptions(reproduction.getHoldings());
        Map<String, List<ReproductionStandardOption>> unavailableStandardOptions =
                getStandardOptionsNotAvailable(reproduction.getHoldings(), reproductionStandardOptions);

        model.addAttribute("reproductionStandardOptions", reproductionStandardOptions);
        model.addAttribute("unavailableStandardOptions", unavailableStandardOptions);
        model.addAttribute("reproductionCustomNotes", reproductions.getAllReproductionCustomNotesAsMap());

        // For new reproduction requests, select the first available option for each holding in the request
        if (!commit)
            autoSelectFirstAvailableOption(reproduction, reproductionStandardOptions, unavailableStandardOptions);

        try {
            if (commit) {
                checkCaptcha(req, result, model); // Make sure a Captcha was entered correctly
                reproductions.createOrEdit(reproduction, null, result, true, false);
                if (!result.hasErrors() && !reproduction.getHoldingReproductions().isEmpty()) {
                    reproductions.autoPrintReproduction(reproduction);
                    return determineNextStep(reproduction, model);
                }
            }
            else {
                reproductions.validateReproductionHoldings(reproduction, null);
            }
        } catch (NoHoldingsException e) {
            throw new ResourceNotFoundException();
        } catch (ClosedException e) {
            model.addAttribute("error", "restricted");
            return "reproduction_error";
        } catch (ClosedForReproductionException e) {
            model.addAttribute("error", "closed");
            return "reproduction_error";
        }

        // If there are suddenly no holding reproductions left, apparently nothing was available
        if (reproduction.getHoldingReproductions().isEmpty()) {
            model.addAttribute("error", "nothingAvailable");
            return "reproduction_error";
        }

        model.addAttribute("reproduction", reproduction);
        return "reproduction_create";
    }

    /**
     * Returns a map of the possible standard reproduction options per holding signature.
     *
     * @param holdings The holdings.
     * @return A map with options per holding.
     */
    private Map<String, List<ReproductionStandardOption>> getStandardReproductionOptions(List<Holding> holdings) {
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                new HashMap<String, List<ReproductionStandardOption>>();
        List<ReproductionStandardOption> standardOptions = reproductions.getAllReproductionStandardOptions();

        for (Holding holding : holdings) {
            List<ReproductionStandardOption> standardOptionsForHolding = new ArrayList<ReproductionStandardOption>();
            if (!holding.allowOnlyCustomReproduction()) {
                for (ReproductionStandardOption standardOption : standardOptions) {
                    if (standardOption.isEnabled() && holding.acceptsReproductionOption(standardOption))
                        standardOptionsForHolding.add(standardOption);
                }
            }
            reproductionStandardOptions.put(holding.getSignature(), standardOptionsForHolding);
        }

        return reproductionStandardOptions;
    }

    /**
     * Returns a map of the unavailable standard reproduction options per holding signature from the given holdings.
     *
     * @param holdings        The holdings.
     * @param standardOptions The possible standard reproduction options per holding signature.
     * @return A map with options per holding.
     */
    private Map<String, List<ReproductionStandardOption>> getStandardOptionsNotAvailable(List<Holding> holdings,
                                                                                         Map<String, List<ReproductionStandardOption>> standardOptions) {
        Map<String, List<ReproductionStandardOption>> unavailableStandardOptions =
                new HashMap<String, List<ReproductionStandardOption>>();

        for (Holding holding : holdings) {
            List<ReproductionStandardOption> unavailableForHolding = new ArrayList<ReproductionStandardOption>();
            if (holding.getStatus() != Holding.Status.AVAILABLE) {
                unavailableForHolding =
                        reproductions.getStandardOptionsNotInSor(holding, standardOptions.get(holding.getSignature()));
            }
            unavailableStandardOptions.put(holding.getSignature(), unavailableForHolding);
        }

        return unavailableStandardOptions;
    }

    /**
     * Returns a map of only the available standard reproduction options per holding signature from the given holdings.
     *
     * @param holdings The holdings.
     * @return A map with options per holding.
     */
    private Map<String, List<ReproductionStandardOption>> getStandardOptionsAvailable(List<Holding> holdings) {
        Map<String, List<ReproductionStandardOption>> availableStandardOptions =
                new HashMap<String, List<ReproductionStandardOption>>();
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                getStandardReproductionOptions(holdings);
        Map<String, List<ReproductionStandardOption>> unavailableStandardOptions =
                getStandardOptionsNotAvailable(holdings, reproductionStandardOptions);

        for (Holding h : holdings) {
            List<ReproductionStandardOption> standardOptions = new ArrayList<ReproductionStandardOption>();
            standardOptions.addAll(reproductionStandardOptions.get(h.getSignature()));
            standardOptions.removeAll(unavailableStandardOptions.get(h.getSignature()));
            availableStandardOptions.put(h.getSignature(), standardOptions);
        }

        return availableStandardOptions;
    }

    /**
     * Auto select the first available standard option for each holding, if any standard options are available.
     *
     * @param reproduction               The reproduction request.
     * @param standardOptions            The standard options.
     * @param unavailableStandardOptions The standard options which are not available.
     */
    private void autoSelectFirstAvailableOption(Reproduction reproduction,
                                                Map<String, List<ReproductionStandardOption>> standardOptions,
                                                Map<String, List<ReproductionStandardOption>> unavailableStandardOptions) {
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            List<ReproductionStandardOption> availableOptions = new ArrayList<ReproductionStandardOption>();
            availableOptions.addAll(standardOptions.get(hr.getHolding().getSignature()));
            availableOptions.removeAll(unavailableStandardOptions.get(hr.getHolding().getSignature()));

            if (!availableOptions.isEmpty())
                hr.setStandardOption(availableOptions.get(0));
        }
    }

    /**
     * After creating a new reproduction, determine the next step.
     * Either the reproduction has to go to the reading room first,
     * or an offer can be created right away allowing the customer to confirm/pay immediately.
     *
     * @param reproduction The reproduction.
     * @param model        The model.
     * @return The view to resolve.
     */
    private String determineNextStep(Reproduction reproduction, Model model) {
        model.asMap().clear();

        if (reproduction.getStatus() == Reproduction.Status.HAS_ORDER_DETAILS) {
            // Mail the confirmation (offer is ready) to the customer
            try {
                reproductionMailer.mailOfferReady(reproduction);
            } catch (MailException me) {
                model.addAttribute("error", "mail");
            }

            return "redirect:/reproduction/confirm/" + reproduction.getId() + "/" + reproduction.getToken();
        }
        else {
            // Mail the reproduction pending details to the customer and inform the reading room
            try {
                reproductionMailer.mailPending(reproduction);
            } catch (MailException me) {
                model.addAttribute("error", "mail");
            }

            model.addAttribute("reproduction", reproduction);

            return "reproduction_pending";
        }
    }

    /**
     * Checks the holdings of a request.
     *
     * @param model   The model to add errors to.
     * @param request The Request with holdings to check.
     * @return Whether no errors were found.
     */
    @Override
    protected boolean checkHoldings(Model model, Request request) {
        if (!super.checkHoldings(model, request)) {
            return false;
        }

        for (HoldingRequest holdingRequest : request.getHoldingRequests()) {
            Holding h = holdingRequest.getHolding();
            Record r = h.getRecord();

            // Determine whether the record is closed for reproduction
            if (r.getCopyright() != null &&
                    (r.getPublicationStatus() == ExternalRecordInfo.PublicationStatus.MINIMAL ||
                            r.getPublicationStatus() == ExternalRecordInfo.PublicationStatus.CLOSED)) {
                model.addAttribute("error", "closed");
                return false;
            }
        }

        return true;
    }

    /**
     * Show the confirmation form of a reproduction.
     *
     * @param req            The HTTP request.
     * @param reproductionId The id of the reproduction.
     * @param token          A token to prevent unauthorized access to the reproduction.
     * @param model          The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/confirm/{reproductionId:[\\d]+}/{token}", method = RequestMethod.GET)
    public String showConfirm(@PathVariable int reproductionId, @PathVariable String token, Model model) {
        Reproduction reproduction = reproductions.getReproductionById(reproductionId);
        validateToken(reproduction, token);
        return processConfirmation(null, reproduction, model, false);
    }

    /**
     * Process the confirmation form of a reproduction.
     *
     * @param req            The HTTP request.
     * @param reproductionId The id of the reproduction.
     * @param token          A token to prevent unauthorized access to the reproduction.
     * @param model          The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/confirm/{reproductionId:[\\d]+}/{token}", method = RequestMethod.POST)
    public String processConfrim(HttpServletRequest req, @PathVariable int reproductionId,
                                 @PathVariable String token, Model model) {
        Reproduction reproduction = reproductions.getReproductionById(reproductionId);
        validateToken(reproduction, token);
        return processConfirmation(req, reproduction, model, true);
    }

    /**
     * Processes the confirmation of a reproduction by the customer.
     *
     * @param req          The HTTP request.
     * @param reproduction The reproduction.
     * @param model        The model to add response attributes to.
     * @param commit       Whether to commit the confirmation to the database and create an order.
     * @return The view to resolve.
     */
    private String processConfirmation(HttpServletRequest req, Reproduction reproduction, Model model, boolean commit) {
        if (reproduction == null)
            throw new InvalidRequestException("No such reproduction.");

        if (reproduction.getStatus().compareTo(Reproduction.Status.HAS_ORDER_DETAILS) < 0)
            throw new InvalidRequestException("Reproduction does not have all of the order details yet.");

        // If the customer already confirmed the reproduction, just redirect to the payment page
        if (reproduction.getStatus() == Reproduction.Status.CONFIRMED) {
            try {
                Order order = reproduction.getOrder();
                if (order == null) {
                    order = reproductions.createOrder(reproduction);

                    // If the reproduction is for free, take care of delivery
                    if (reproduction.isForFree()) {
                        // Determine if we can move up to either 'completed' or 'active' immediatly
                        changeStatusAfterPayment(reproduction);

                        // Show payment accepted page
                        return "redirect:/reproduction/order/confirm";
                    }
                }

                return "redirect:" + payWayService.getPaymentPageRedirectLink(order.getId());
            } catch (IncompleteOrderDetailsException onre) {
                // We already checked for this one though
                throw new InvalidRequestException("Reproduction is not ready yet.");
            } catch (OrderRegistrationFailureException orfe) {
                String msg = msgSource.getMessage("payway.error", null, LocaleContextHolder.getLocale());
                throw new InvalidRequestException(msg);
            }
        }

        // If already moved on from the status 'confirmed', the customer has no business on this page anymore
        if (reproduction.getStatus().compareTo(Reproduction.Status.CONFIRMED) >= 0)
            throw new InvalidRequestException("Reproduction has been confirmed already.");

        model.addAttribute("reproduction", reproduction);
        if (commit) {
            // Did the customer accept the terms and conditions?
            String accept = req.getParameter("accept_terms_conditions");
            if (!"accept".equals(accept)) {
                String msg = msgSource.getMessage("accept.error", null, LocaleContextHolder.getLocale());
                model.addAttribute("acceptError", msg);
                return "reproduction_confirm";
            }

            try {
                // Change status to 'confirmed by customer' and create order
                reproductions.updateStatusAndAssociatedHoldingStatus(reproduction, Reproduction.Status.CONFIRMED);
                Order order = reproductions.createOrder(reproduction);

                // If the reproduction is for free, take care of delivery
                if (reproduction.isForFree()) {
                    // Determine if we can move up to either 'completed' or 'active' immediatly
                    changeStatusAfterPayment(reproduction);

                    // Show payment accepted page
                    return "redirect:/reproduction/order/confirm";
                }

                // Otherwise redirect the user to the payment page
                return "redirect:" + payWayService.getPaymentPageRedirectLink(order.getId());
            } catch (IncompleteOrderDetailsException onre) {
                // We already checked for this one though
                throw new InvalidRequestException("Reproduction is not ready yet.");
            } catch (OrderRegistrationFailureException orfe) {
                String msg = msgSource.getMessage("payway.error", null, LocaleContextHolder.getLocale());
                model.addAttribute("paywayError", msg);
            }
        }

        return "reproduction_confirm";
    }

    /**
     * Reproduction confirmed, no payment required.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/order/confirm", method = RequestMethod.GET)
    public String confirm() {
        return "reproduction_order_confirm";
    }

    /**
     * PayWay response, payment was accepted.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/order/accept", method = RequestMethod.GET)
    public String accept() {
        LOGGER.debug(String.format("/reproduction/order/accept : Called order accept."));
        return "reproduction_order_accept";
    }

    /**
     * A one time PayWay response after the payment has been made, in our case, to send an email.
     */
    @RequestMapping(value = "/order/accept", method = RequestMethod.GET, params = "POST")
    public HttpStatus accept(@RequestParam Map<String, String> requestParams) {
        PayWayMessage payWayMessage = new PayWayMessage(requestParams);

        LOGGER.debug(String.format(
                "/reproduction/order/accept : Called POST order accept with message %s", payWayMessage));

        // Make sure the message is valid
        if (!payWayService.isValid(payWayMessage)) {
            LOGGER.debug(String.format(
                    "/reproduction/order/accept : Invalid signature for message %s", payWayMessage));
            return HttpStatus.BAD_REQUEST;
        }

        // Check the reproduction ...
        Integer reproductionId = payWayMessage.getInteger("userid");
        Reproduction reproduction = reproductions.getReproductionById(reproductionId);
        if (reproduction == null) {
            LOGGER.debug(String.format(
                    "/reproduction/order/accept : Reproduction not found for message %s", payWayMessage));
            return HttpStatus.BAD_REQUEST;
        }

        // ... and the order
        Integer orderId = payWayMessage.getInteger("orderid");
        Order order = reproduction.getOrder();
        if (order.getId() != orderId) {
            LOGGER.debug(String.format(
                    "/reproduction/order/accept : Reproduction order id does not match order id in message %s",
                    payWayMessage));
            return HttpStatus.BAD_REQUEST;
        }

        // Everything is fine, change status and send email to customer
        reproductions.refreshOrder(order);
        changeStatusAfterPayment(reproduction);

        reproductions.saveReproduction(reproduction);
        return HttpStatus.OK;
    }

    /**
     * PayWay response, payment was canceled.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/order/cancel", method = RequestMethod.GET)
    public String cancel() {
        return "reproduction_order_cancel";
    }

    /**
     * PayWay response, payment was declined.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/order/decline", method = RequestMethod.GET)
    public String decline() {
        return "reproduction_order_decline";
    }

    /**
     * PayWay response, exception occurred during payment.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/order/exception", method = RequestMethod.GET)
    public String exception() {
        return "reproduction_order_exception";
    }

    /**
     * Determine if we can move up to either 'completed' or 'active' immediatly.
     *
     * @param reproduction The reproduction.
     */
    private void changeStatusAfterPayment(Reproduction reproduction) {
        reproductions.updateStatusAndAssociatedHoldingStatus(reproduction, Reproduction.Status.ACTIVE);
        if (reproduction.isCompletelyInSor())
            reproductions.updateStatusAndAssociatedHoldingStatus(reproduction, Reproduction.Status.COMPLETED);
    }

    /**
     * Update a reproduction.
     *
     * @param id    ID of the reproduction to fetch.
     * @param model Passed view model.
     * @return The name of the view to use.
     */
    @RequestMapping(value = "/{id:[\\d]+}/edit", method = RequestMethod.GET)
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String showEditForm(@PathVariable int id, Model model) {
        Reproduction r = reproductions.getReproductionById(id);
        if (r == null)
            throw new ResourceNotFoundException();

        // It is not allowed to modify a reproduction after confirmation by the customer
        if (r.getStatus().ordinal() >= Reproduction.Status.CONFIRMED.ordinal()) {
            model.addAttribute("error", "confirmed");
            return "reproduction_error";
        }

        model.addAttribute("original", r);
        model.addAttribute("reproduction", r);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(r.getHoldings()));

        return "reproduction_mass_create";
    }

    /**
     * Save the reproduction.
     *
     * @param id           ID of the reproduction to fetch.
     * @param reproduction The reproduction.
     * @param result       The object to save the validation errors.
     * @param free         Whether this reproduction is for free.
     * @param mail         Whether or not to mail a reproduction confirmation.
     * @param model        The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/{id:[\\d]+}/edit", method = RequestMethod.POST)
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String processEditForm(@PathVariable int id, @ModelAttribute("reproduction") Reproduction reproduction,
                                  BindingResult result, boolean free, boolean mail, Model model) {
        Reproduction originalReproduction = reproductions.getReproductionById(id);
        if (originalReproduction == null)
            throw new ResourceNotFoundException();

        // It is not allowed to modify a reproduction after confirmation by the customer
        if (reproduction.getStatus().ordinal() >= Reproduction.Status.CONFIRMED.ordinal()) {
            model.addAttribute("error", "confirmed");
            return "reproduction_error";
        }

        try {
            reproductions.createOrEdit(reproduction, originalReproduction, result, false, free);
            if (!result.hasErrors()) {
                // Mail the confirmation (offer is ready) to the customer
                boolean mailSuccess = true;
                if (mail) {
                    try {
                        // Determine which one was updated
                        Reproduction r = (originalReproduction == null) ? reproduction : originalReproduction;
                        reproductionMailer.mailOfferReady(r);
                    } catch (MailException me) {
                        mailSuccess = false;
                    }
                }
                return "redirect:/reproduction/" + originalReproduction.getId() + (!mailSuccess ? "?mail=error" : "");
            }
        } catch (ClosedException e) {
            String msg = msgSource.getMessage("reproduction.error.restricted", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        } catch (NoHoldingsException e) {
            String msg = msgSource.getMessage("reproduction.error.noHoldings", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        } catch (ClosedForReproductionException e) {
            String msg = msgSource.getMessage("reproduction.error.restricted", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        }

        model.addAttribute("original", originalReproduction);
        model.addAttribute("reproduction", reproduction);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(reproduction.getHoldings()));

        return "reproduction_mass_create";
    }

    /**
     * Create a reproduction without restrictions of size or usage.
     *
     * @param fromReproductionId The id of a reproduction to use as a base of this new reproduction,
     *                           if applicable (not required).
     * @param model              The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform", method = RequestMethod.GET)
    @Secured("ROLE_REPRODUCTION_CREATE")
    public String showMassCreateForm(@RequestParam(required = false) Integer fromReproductionId, Model model) {
        Reproduction newReproduction = new Reproduction();
        if (fromReproductionId != null) {
            Reproduction fromReproduction = reproductions.getReproductionById(fromReproductionId);
            if (fromReproduction != null) {
                newReproduction.setCustomerEmail(fromReproduction.getCustomerEmail());
                newReproduction.setCustomerName(fromReproduction.getCustomerName());
            }
        }
        model.addAttribute("reproduction", newReproduction);

        // Add all available standard reproduction options to the model
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                getStandardOptionsAvailable(newReproduction.getHoldings());

        model.addAttribute("reproductionStandardOptions", reproductionStandardOptions);

        return "reproduction_mass_create";
    }

    /**
     * Process the search for new holdings to add to the mass reproduction.
     *
     * @param newReproduction The already semi-built reproduction.
     * @param searchTitle     The keywords to search for in the title.
     * @param searchSignature The keywords to search for in the signature.
     * @param model           The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform", method = RequestMethod.POST, params = "searchSubmit")
    @Secured("ROLE_REPRODUCTION_CREATE")
    public String processSearchMassCreateForm(@ModelAttribute("reproduction") Reproduction newReproduction,
                                              @RequestParam String searchTitle, @RequestParam String searchSignature,
                                              Model model) {
        List<Holding> holdingList = searchMassCreate(newReproduction, searchTitle, searchSignature);

        model.addAttribute("reproduction", newReproduction);
        model.addAttribute("holdingList", holdingList);

        List<Holding> holdings = new ArrayList<Holding>();
        holdings.addAll(newReproduction.getHoldings());
        holdings.addAll(holdingList);

        // Add all available standard reproduction options to the model
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                getStandardOptionsAvailable(holdings);

        model.addAttribute("reproductionStandardOptions", reproductionStandardOptions);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(holdings));

        return "reproduction_mass_create";
    }

    /**
     * Save the new mass reproduction.
     *
     * @param newReproduction The already semi-built reproduction.
     * @param result          The object to save the validation errors.
     * @param searchTitle     The keywords to search for in the title.
     * @param searchSignature The keywords to search for in the signature.
     * @param free            Whether this reproduction is for free.
     * @param mail            Whether or not to mail a reproduction confirmation.
     * @param model           The model to add attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/masscreateform", method = RequestMethod.POST)
    @Secured("ROLE_REPRODUCTION_CREATE")
    public String processMassCreateForm(@ModelAttribute("reproduction") Reproduction newReproduction,
                                        BindingResult result, @RequestParam String searchTitle,
                                        @RequestParam(required = false) String searchSignature,
                                        boolean free, boolean mail, Model model) {
        List<Holding> holdingList = searchMassCreate(newReproduction, searchTitle, searchSignature);

        try {
            reproductions.createOrEdit(newReproduction, null, result, false, free);
            if (!result.hasErrors()) {
                reproductions.autoPrintReproduction(newReproduction);

                // Mail the confirmation (offer is ready) to the customer
                boolean mailSuccess = true;
                if (mail) {
                    try {
                        reproductionMailer.mailOfferReady(newReproduction);
                    } catch (MailException me) {
                        mailSuccess = false;
                    }
                }
                return "redirect:/reproduction/" + newReproduction.getId() + (!mailSuccess ? "?mail=error" : "");
            }
        } catch (ClosedException e) {
            String msg = msgSource.getMessage("reproduction.error.restricted", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        } catch (NoHoldingsException e) {
            String msg = msgSource.getMessage("reproduction.error.noHoldings", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        } catch (ClosedForReproductionException e) {
            String msg = msgSource.getMessage("reproduction.error.restricted", null, "",
                    LocaleContextHolder.getLocale());
            result.addError(new ObjectError(result.getObjectName(), null, null, msg));
        }

        model.addAttribute("reproduction", newReproduction);
        model.addAttribute("holdingList", holdingList);

        List<Holding> holdings = new ArrayList<Holding>();
        holdings.addAll(newReproduction.getHoldings());
        holdings.addAll(holdingList);

        // Add all available standard reproduction options to the model
        Map<String, List<ReproductionStandardOption>> reproductionStandardOptions =
                getStandardOptionsAvailable(holdings);

        model.addAttribute("reproductionStandardOptions", reproductionStandardOptions);
        model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(holdings));

        return "reproduction_mass_create";
    }

    /**
     * Displays all standard reproduction options for editing.
     *
     * @param model The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/standardoptions", method = RequestMethod.GET)
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String showStandardOptions(Model model) {
        ReproductionStandardOptions standardOptions = new ReproductionStandardOptions(
                reproductions.getAllReproductionStandardOptions(), reproductions.getAllReproductionCustomNotes());
        model.addAttribute("standardOptions", standardOptions);
        return "reproduction_standard_options_edit";
    }

    /**
     * Updates all standard reproductions options.
     *
     * @param model           The model to add response attributes to.
     * @param result          he object to save the validation errors.
     * @param standardOptions The standard reproduction options.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/standardoptions", method = RequestMethod.POST)
    @Secured("ROLE_REPRODUCTION_MODIFY")
    public String editStandardOptions(@ModelAttribute("standardOptions") ReproductionStandardOptions standardOptions,
                                      BindingResult result, Model model) {
        reproductions.editStandardOptions(standardOptions, result);
        model.addAttribute("standardOptions", standardOptions);
        return "reproduction_standard_options_edit";
    }

    /**
     * Validates the token bound to a reproduction.
     *
     * @param reproduction The reproduction.
     * @param token        The token.
     */
    private void validateToken(Reproduction reproduction, String token) {
        if ((reproduction != null) && !reproduction.getToken().equalsIgnoreCase(token)) {
            throw new InvalidRequestException("Invalid token provided.");
        }
    }
}
