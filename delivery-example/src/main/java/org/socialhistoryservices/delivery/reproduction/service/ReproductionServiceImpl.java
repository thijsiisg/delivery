package org.socialhistoryservices.delivery.reproduction.service;

import org.apache.log4j.Logger;
import org.socialhistoryservices.delivery.api.*;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reproduction.dao.HoldingReproductionDAO;
import org.socialhistoryservices.delivery.reproduction.dao.OrderDAO;
import org.socialhistoryservices.delivery.reproduction.dao.ReproductionDAO;
import org.socialhistoryservices.delivery.reproduction.dao.ReproductionStandardOptionDAO;
import org.socialhistoryservices.delivery.reproduction.entity.*;
import org.socialhistoryservices.delivery.reproduction.entity.Order;
import org.socialhistoryservices.delivery.reproduction.util.ReproductionStandardOptions;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.persistence.criteria.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Represents the service of the reproduction package.
 */
@Service
@Transactional
public class ReproductionServiceImpl extends AbstractRequestService implements ReproductionService {
    @Autowired
    private ReproductionDAO reproductionDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private HoldingReproductionDAO holdingReproductionDAO;

    @Autowired
    private ReproductionStandardOptionDAO reproductionStandardOptionDAO;

    @Autowired
    private PayWayService payWayService;

    @Autowired
    private SharedObjectRepositoryService sorService;

    @Autowired
    private BeanFactory bf;

    @Autowired
    @Qualifier("myCustomProperties")
    private Properties properties;

    private Logger log = Logger.getLogger(getClass());

    /**
     * Add a Reproduction to the database.
     *
     * @param obj Reproduction to add.
     */
    public void addReproduction(Reproduction obj) {
        // Make sure the holdings get set to the correct status.
        updateStatusAndAssociatedHoldingStatus(obj, obj.getStatus());

        // Add to the database
        reproductionDAO.add(obj);
    }

    /**
     * Remove a Reproduction from the database.
     *
     * @param obj Reproduction to remove.
     */
    public void removeReproduction(Reproduction reproduction) {
        // Set all holdings linked to this reproduction back to AVAILABLE if this is a PAYED/ACTIVE reproduction.
        // Note that we are in a transaction here, so it does not matter the records are still linked
        // to the reproduction when setting them to available.
        if ((reproduction.getStatus() == Reproduction.Status.PAYED) ||
                (reproduction.getStatus() == Reproduction.Status.ACTIVE)) {
            changeHoldingStatus(reproduction, Holding.Status.AVAILABLE);
        }
        reproductionDAO.remove(reproduction);
    }

    /**
     * Change the status of all holdings in a reproduction.
     *
     * @param reproduction Reproduction to change status for.
     * @param status       Status to change holdings to.
     */
    public void changeHoldingStatus(Reproduction reproduction, Holding.Status status) {
        super.changeHoldingStatus(reproduction, status);
        saveReproduction(reproduction);
    }

    /**
     * Save changes to a Reproduction in the database.
     *
     * @param obj Reproduction to save.
     */
    public void saveReproduction(Reproduction obj) {
        reproductionDAO.save(obj);
    }

    /**
     * Add/update the standard reproduction options.
     *
     * @param standardOptions The standard reproduction options.
     */
    private void addOrUpdateStandardOptions(ReproductionStandardOptions standardOptions) {
        for (ReproductionStandardOption option1 : standardOptions.getOptions()) {
            boolean has = false;
            for (ReproductionStandardOption option2 : getAllReproductionStandardOptions()) {
                if (option1.getId() == option2.getId()) {
                    option2.mergeWith(option1);
                    reproductionStandardOptionDAO.save(option2);
                    has = true;
                }
            }

            if (!has) {
                reproductionStandardOptionDAO.add(option1);
            }
        }
    }

    /**
     * Retrieve the Reproduction matching the given id.
     *
     * @param id Id of the Reproduction to retrieve.
     * @return The Reproduction matching the id.
     */
    public Reproduction getReproductionById(int id) {
        return reproductionDAO.getById(id);
    }

    /**
     * Retrieve the ReproductionStandardOption matching the given id.
     *
     * @param id Id of the ReproductionStandardOption to retrieve.
     * @return The ReproductionStandardOption matching the id.
     */
    public ReproductionStandardOption getReproductionStandardOptionById(int id) {
        return reproductionStandardOptionDAO.getById(id);
    }

    /**
     * List all Reproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching Reproductions.
     */
    public List<Reproduction> listReproductions(CriteriaQuery<Reproduction> q) {
        return reproductionDAO.list(q);
    }

    /**
     * List all HoldingReproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching HoldingReproductions.
     */
    public List<HoldingReproduction> listHoldingReproductions(CriteriaQuery<HoldingReproduction> q) {
        return holdingReproductionDAO.list(q);
    }

    /**
     * Returns all standard options for reproductions.
     *
     * @return A list with all standard options for reproductions.
     */
    public List<ReproductionStandardOption> getAllReproductionStandardOptions() {
        return reproductionStandardOptionDAO.listAll();
    }

    /**
     * Get a criteria builder for querying Reproductions.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReproductionCriteriaBuilder() {
        return reproductionDAO.getCriteriaBuilder();
    }

    /**
     * Get a criteria builder for querying HoldingReproductions.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getHoldingReproductionCriteriaBuilder() {
        return holdingReproductionDAO.getCriteriaBuilder();
    }

    /**
     * Mark a specific item in a reproduction as seen, bumping it to the next status.
     *
     * @param r Reproduction to change status for.
     * @param h Holding to bump.
     */
    public void markItem(Reproduction r, Holding h) {
        // Ignore old reproductions
        if (r == null)
            return;

        super.markItem(h);

        markReproduction(r);
        saveReproduction(r);
    }

    /**
     * Mark a reproduction, bumping it to the next status.
     *
     * @param r Reproduction to change status for.
     */
    public void markRequest(Reproduction r) {
        // Ignore old reproductions
        if (r == null)
            return;

        markReproduction(r);
        saveReproduction(r);
    }

    /**
     * Mark a reproduction, bumping it to the next status.
     *
     * @param r Reproduction to change status for.
     */
    private void markReproduction(Reproduction r) {
        boolean complete = true;
        for (HoldingReproduction hr : r.getHoldingReproductions()) {
            if (!hr.isCompleted()) {
                Holding holding = hr.getHolding();
                if (holding.getStatus() == Holding.Status.RETURNED) {
                    hr.setCompleted(true);
                } else {
                    complete = false;
                }
            }
        }

        if (complete) {
            r.setStatus(Reproduction.Status.COMPLETED);
        } else {
            r.setStatus(Reproduction.Status.ACTIVE);
        }
    }

    /**
     * Merge the other reproduction's fields into this reproduction.
     *
     * @param reproduction The reproduction.
     * @param other        The other reproduction.
     */
    public void merge(Reproduction reproduction, Reproduction other) {
        reproduction.setCustomerName(other.getName());
        reproduction.setCustomerEmail(other.getEmail());
        reproduction.setPrinted(other.isPrinted());
        reproduction.setRequestLocale(other.getRequestLocale());
        reproduction.setDateHasOrderDetails(other.getDateHasOrderDetails());
        reproduction.setDeliveryTimeComment(other.getDeliveryTimeComment());
        reproduction.setComment(other.getComment());

        if (other.getHoldingReproductions() == null) {
            for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
                requests.updateHoldingStatus(hr.getHolding(), Holding.Status.AVAILABLE);
            }
            reproduction.setHoldingReproductions(new ArrayList<HoldingReproduction>());
        } else {
            // Delete holdings that were not provided.
            deleteHoldingsNotInProvidedRequest(reproduction, other);

            // Add/update provided.
            addOrUpdateHoldingsProvidedByRequest(reproduction, other);
        }
        updateStatusAndAssociatedHoldingStatus(reproduction, other.getStatus());
    }

    /**
     * Set the reproduction status and update the associated holdings status accordingly.
     * Only updates status forward.
     *
     * @param reproduction The reproduction.
     * @param status       The reproduction which changed status.
     */
    public void updateStatusAndAssociatedHoldingStatus(Reproduction reproduction, Reproduction.Status status) {
        if (status.ordinal() < reproduction.getStatus().ordinal()) {
            return;
        }

        reproduction.setStatus(status);

        if (status == Reproduction.Status.HAS_ORDER_DETAILS)
            reproduction.setDateHasOrderDetails(new Date());

        Holding.Status hStatus = null;
        boolean completedStatus = false;
        switch (status) {
            case ACTIVE:
                hStatus = Holding.Status.IN_USE;
                break;
            case COMPLETED:
            case DELIVERED:
            case CANCELLED:
                completedStatus = true;
                hStatus = Holding.Status.AVAILABLE;
                break;
        }

        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            if (!hr.isCompleted()) {
                hr.setCompleted(completedStatus);
                requests.updateHoldingStatus(hr.getHolding(), hStatus);
            }
            else if (!hr.isOnHold()) {
                requests.updateHoldingStatus(hr.getHolding(), hStatus);
            }
        }
    }

    /**
     * Adds a HoldingRequest to the HoldingRequests assoicated with this request.
     *
     * @param request
     * @param holdingRequest The HoldingRequests to add.
     */
    protected void addToHoldingRequests(Request request, HoldingRequest holdingRequest) {
        Reproduction reproduction = (Reproduction) request;
        HoldingReproduction holdingReproduction = (HoldingReproduction) holdingRequest;

        holdingReproduction.setReproduction(reproduction);
        reproduction.getHoldingReproductions().add(holdingReproduction);
    }

    /**
     * Prints a reproduction by using the default printer.
     *
     * @param reproduction The reproduction to print.
     * @param alwaysPrint  If set to true, already printed reproductions will also be printed.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed
     *                          (or ran out of paper for example).
     */
    public void printReproduction(Reproduction reproduction, boolean alwaysPrint) throws PrinterException {
        try {
            List<RequestPrintable> requestPrintables = new ArrayList<RequestPrintable>();
            for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
                ReproductionPrintable rp = new ReproductionPrintable(
                        hr, msgSource, (DateFormat) bf.getBean("dateFormat"), properties);
                requestPrintables.add(rp);
            }

            printRequest(reproduction, requestPrintables, alwaysPrint);
        } catch (PrinterException e) {
            log.warn("Printing reproduction failed", e);
            throw e;
        }

        saveReproduction(reproduction);
    }

    /**
     * Print a reproduction if it was not printed yet.
     *
     * @param reproduction The reproduction to print.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed
     *                          (or ran out of paper for example).
     */
    public void printReproduction(Reproduction reproduction) throws PrinterException {
        printReproduction(reproduction, false);
    }

    /**
     * Edit reproductions.
     *
     * @param newRes     The new reproduction to put in the database.
     * @param oldRes     The old reproduction in the database (if present).
     * @param result     The binding result object to put the validation errors in.
     * @param isCustomer Whether the customer is creating the reproduction.
     * @throws org.socialhistoryservices.delivery.request.service.ClosedException     Thrown when a holding is provided which
     *                                                                                references a record which is restrictionType=CLOSED.
     * @throws org.socialhistoryservices.delivery.request.service.NoHoldingsException Thrown when no holdings are provided.
     */
    public void createOrEdit(Reproduction newReproduction, Reproduction oldReproduction, BindingResult result,
                             boolean isCustomer) throws ClosedException, NoHoldingsException {
        // Validate the reproduction.
        try {
            validateRequest(newReproduction, result);
            validateHoldings(newReproduction, oldReproduction, false);

            if (!isCustomer) {
                validateReproductionNotCustomer(newReproduction, result);
            }
        } catch (InUseException iue) {
            // We can ignore this, as it won't be thrown anyway, or at least, it shouldn't
            // TODO: Log if thrown!
        }

        // Initialize the holdings of this reproduction.
        initHoldingReproductions(newReproduction, isCustomer);

        // Move status forward if all order details are known already
        if (hasOrderDetails(newReproduction) && hasOnlyAvailableHoldings(newReproduction)) {
            updateStatusAndAssociatedHoldingStatus(newReproduction, Reproduction.Status.HAS_ORDER_DETAILS);
        }

        // Reserve all holdings that are currently available
        reserveAvailableHoldings(newReproduction);

        // Add or save the record when no errors are present.
        if (!result.hasErrors()) {
            if (oldReproduction == null) {
                newReproduction.setCreationDate(new Date());
                addReproduction(newReproduction);
            } else {
                merge(oldReproduction, newReproduction);
                saveReproduction(oldReproduction);
            }
        }
    }

    /**
     * Determine whether a wish for a holding reproduction is in the SOR.
     *
     * @param holdingReproduction The holding reproduction.
     * @return Whether a wish for a holding reproduction is in the SOR.
     */
    private boolean isHoldingReproductionInSor(HoldingReproduction holdingReproduction) {
        ReproductionStandardOption standardOption = holdingReproduction.getStandardOption();
        if (standardOption != null) {
            Holding holding = holdingReproduction.getHolding();
            SorMetadata sorMetadata = sorService.getMasterMetadataForPid(holding.determinePid());

            return (sorMetadata != null);
        }
        return false;
    }

    /**
     * Whether the price and delivery time is determined for all holdings and
     * as a result the reproduction has all the order details.
     *
     * @param reproduction The reproduction.
     * @return Whether all holdings have order details.
     */
    private boolean hasOrderDetails(Reproduction reproduction) {
        List<HoldingReproduction> hrs = reproduction.getHoldingReproductions();
        if ((hrs == null) || hrs.isEmpty())
            return false;

        boolean hasOrderDetails = true;
        for (HoldingReproduction hr : hrs) {
            if (!hr.hasOrderDetails())
                hasOrderDetails = false;
        }

        return hasOrderDetails;
    }

    /**
     * Whether all holdings of the reproduction are currently available.
     * Takes only holdings into account of which there is no digital object in the SOR.
     *
     * @param reproduction The reproduction.
     * @return Whether all holdings of the reproduction are currently available.
     */
    private boolean hasOnlyAvailableHoldings(Reproduction reproduction) {
        List<Holding> holdings = reproduction.getHoldings();
        if ((holdings == null) || holdings.isEmpty())
            return false;

        boolean hasOnlyAvailableHoldings = true;
        for (Holding h : holdings) {
            if (h.getStatus() != Holding.Status.AVAILABLE)
                hasOnlyAvailableHoldings = false;
        }

        return hasOnlyAvailableHoldings;
    }

    /**
     * Reserve all the holdings of the given reproduction that are currently available.
     *
     * @param reproduction The reproduction.
     */
    private void reserveAvailableHoldings(Reproduction reproduction) {
        for (Holding h : reproduction.getHoldings()) {
            if (h.getStatus() == Holding.Status.AVAILABLE)
                requests.updateHoldingStatus(h, Holding.Status.RESERVED);
        }
    }

    /**
     * Scheduled task to cancel all reproductions not payed within 5 days after the offer was ready.
     */
    @Scheduled(cron = "0 0 * * * MON-FRI")
    public void checkPayedReproductions() {
        // Determine the number of days
        String nrOfDays = properties.getProperty("prop_daysPaymentReproduction", "5");
        Integer nrDays = Integer.parseInt(nrOfDays);

        // Determine the date that many days ago
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -nrDays);

        // Build the query
        CriteriaBuilder builder = getReproductionCriteriaBuilder();

        CriteriaQuery<Reproduction> query = builder.createQuery(Reproduction.class);
        Root<Reproduction> reproductionRoot = query.from(Reproduction.class);
        query.select(reproductionRoot);

        Expression<Boolean> criteria = builder.lessThan(
                reproductionRoot.get(Reproduction_.dateHasOrderDetails),
                calendar.getTime()
        );
        query.where(criteria);

        // Cancel all found reproductions
        for (Reproduction reproduction : listReproductions(query)) {
            reproduction.setStatus(Reproduction.Status.CANCELLED);

            // Undo the reservation of the holdings
            for (Holding holding : reproduction.getHoldings()) {
                requests.updateHoldingStatus(holding, Holding.Status.AVAILABLE);
            }

            saveReproduction(reproduction);
        }
    }

    /**
     * Creates an order for the given reproduction.
     *
     * @param r The reproduction.
     * @return The created and registered order. (Or null if the reproduction is for free)
     * @throws IncompleteOrderDetailsException   Thrown when not all holdings have an order ready.
     * @throws OrderRegistrationFailureException Thrown in case we failed to register the order in PayWay.
     */
    public Order createOrder(Reproduction r) throws IncompleteOrderDetailsException, OrderRegistrationFailureException {
        if (!hasOrderDetails(r))
            throw new IncompleteOrderDetailsException();

        // Check if we maybe already created an order before
        Order order = r.getOrder();
        if (order != null)
            return order;

        // No need to create an order if the order is for free
        if (r.isForFree()) {
            updateStatusAndAssociatedHoldingStatus(r, Reproduction.Status.PAYED);
            reproductionDAO.save(r);
            return null;
        }

        // First attempt to create and register a new order in PayWay
        try {
            // PayWay wants the amounts in number of cents
            BigDecimal price = r.getTotalPrice();
            long amount = price.movePointRight(2).longValue();

            PayWayMessage message = new PayWayMessage();
            message.put("amount", amount);
            message.put("currency", "EUR");
            message.put("language", LocaleContextHolder.getLocale().toString().equals("en") ? "en_US" : "nl_NL");
            message.put("cn", r.getCustomerName());
            message.put("email", r.getCustomerEmail());
            message.put("owneraddress", null);
            message.put("ownerzip", null);
            message.put("ownertown", null);
            message.put("ownercty", null);
            message.put("ownertelno", null);
            message.put("com", "Delivery reproduction " + r.getId());
            message.put("paymentmethod", PayWayMessage.ORDER_OGONE_PAYMENT);
            message.put("userid", r.getId()); // We'll use the user id as a link to our reproduction

            PayWayMessage orderMessage = payWayService.send("createOrder", message);

            // We received an message from PayWay, so the order is registered
            order = new Order();
            order.setId(orderMessage.getLong("orderid"));

            r.setOrder(order);
            reproductionDAO.save(r);

            // Refresh the actual order details asynchronously
            refreshOrder(order);
        } catch (InvalidPayWayMessageException ipwme) {
            log.error("Invalid or no PayWay message received when registering a new order.", ipwme);
            throw new OrderRegistrationFailureException(ipwme);
        }

        return order;
    }

    /**
     * Will refresh the given order by retrieving the order details from PayWay.
     * The API call is performed in a seperate thread and
     * a Future object is returned to see when and whether the refresh was succesful.
     *
     * @param order The order to refresh. The id must be set.
     * @return A Future object that will return the refreshed Order when succesful.
     */
    //@Async
    public Future<Order> refreshOrder(Order order) {
        try {
            PayWayMessage message = payWayService.getMessageForOrderId(order.getId());
            PayWayMessage orderDetails = payWayService.send("orderDetails", message);

            order.mapFromPayWayMessage(orderDetails);
            orderDAO.save(order);

            return new AsyncResult<Order>(order);
        } catch (InvalidPayWayMessageException ivwme) {
            log.debug(String.format("refreshOrder() : Failed to refresh the order with id %d", order.getId()));
            return new AsyncResult<Order>(null);
        }
    }

    /**
     * Validate a request using the provided binding result to store errors.
     *
     * @param request The request.
     * @param result  The binding result.
     */
    @Override
    protected void validateRequest(Request request, BindingResult result) {
        super.validateRequest(request, result);
        Reproduction reproduction = (Reproduction) request;

        // Validate associated HoldingReproductions if present.
        int i = 0;
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            // If a standard option is chosen, then ignore the provided values
            if (hr.getStandardOption() != null) {
                hr.setPrice(null);
                hr.setDeliveryTime(null);
                hr.setCustomReproductionCustomer(null);
                hr.setCustomReproductionReply(null);
            }

            if ((hr.getStandardOption() == null) && (hr.getCustomReproductionCustomer() == null)) {
                result.addError(new FieldError(result.getObjectName(),
                        "holdingReproductions[" + i + "].customReproductionCustomer", "", false,
                        new String[]{"validator.customReproductionCustomer"}, null, "Required"));
            }
            i++;
        }
    }

    /**
     * In case the reproduction is not from the customer, then the price and expected delivery time
     * of the custom reproductions have to be given.
     *
     * @param reproduction The reproduction.
     * @param result       The binding result.
     */
    private void validateReproductionNotCustomer(Reproduction reproduction, BindingResult result) {
        // Validate associated HoldingReproductions if present.
        int i = 0;
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            if ((hr.getStandardOption() == null) && (hr.getPrice() != null)
                    && (hr.getPrice().compareTo(BigDecimal.ZERO) < 0)) {
                result.addError(new FieldError(result.getObjectName(), "holdingReproductions[" + i + "].price",
                        hr.getPrice(), false, new String[]{"validator.price"}, null, "Required"));
            }

            if ((hr.getStandardOption() == null) && (hr.getDeliveryTime() != null) && (hr.getDeliveryTime() <= 0)) {
                result.addError(new FieldError(result.getObjectName(), "holdingReproductions[" + i + "].deliveryTime",
                        hr.getDeliveryTime(), false, new String[]{"validator.deliveryTime"}, null, "Required"));
            }

            i++;
        }
    }

    /**
     * Initializes the holding reproductions.
     * Determines if we can already state the price and delivery time for one or more chosen holdings.
     *
     * @param reproduction The reproduction.
     * @param isCustomer   Whether the customer is creating the reproduction.
     */
    private void initHoldingReproductions(Reproduction reproduction, boolean isCustomer) {
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            ReproductionStandardOption standardOption = hr.getStandardOption();

            // Determine if we can specify the price and delivery time, but have not done so yet
            if ((standardOption != null) && ((hr.getPrice() == null) || (hr.getDeliveryTime() == null))) {
                // Authenticated users (IISH users) do not have to pay
                if (isCustomer && SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
                    hr.setPrice(BigDecimal.ZERO);
                else
                    hr.setPrice(standardOption.getPrice());

                hr.setDeliveryTime(standardOption.getDeliveryTime());
            }
        }
    }

    /**
     * Validates and saves the standard reproduction options.
     *
     * @param standardOptions The standard reproduction options.
     * @param result          The binding result object to put the validation errors in.
     */
    public void editStandardOptions(ReproductionStandardOptions standardOptions, BindingResult result) {
        validateStandardOptions(standardOptions, result);

        // Add or save the records when no errors are present
        if (!result.hasErrors()) {
            addOrUpdateStandardOptions(standardOptions);
        }
    }

    /**
     * Validate reproduction standard options using the provided binding result to store errors.
     *
     * @param standardOptions The standard reproduction options.
     * @param result          The binding result.
     */
    private void validateStandardOptions(ReproductionStandardOptions standardOptions, BindingResult result) {
        int i = 0;
        for (ReproductionStandardOption standardOption : standardOptions.getOptions()) {
            result.pushNestedPath("options[" + i + "]");
            validator.validate(standardOption, result);
            result.popNestedPath();
            i++;
        }
    }

    /**
     * Returns the active reproduction with which this holding is associated.
     *
     * @param h      The Holding to get the active reproduction of
     * @param getAll Whether to return all active reproductions (0)
     *               or only those that are on hold (< 0) or those that are NOT on hold (> 0).
     * @return The active reproduction, or null if no active reproduction exists
     */
    public Reproduction getActiveFor(Holding h, int getAll) {
        return reproductionDAO.getActiveFor(h, getAll);
    }
}
