package org.socialhistoryservices.delivery.reproduction.service;

import org.apache.log4j.Logger;
import org.socialhistoryservices.delivery.api.InvalidPayWayMessageException;
import org.socialhistoryservices.delivery.api.PayWayMessage;
import org.socialhistoryservices.delivery.api.PayWayService;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reproduction.dao.HoldingReproductionDAO;
import org.socialhistoryservices.delivery.reproduction.dao.OrderDAO;
import org.socialhistoryservices.delivery.reproduction.dao.ReproductionDAO;
import org.socialhistoryservices.delivery.reproduction.dao.ReproductionStandardOptionDAO;
import org.socialhistoryservices.delivery.reproduction.entity.*;
import org.socialhistoryservices.delivery.reproduction.entity.Order;
import org.socialhistoryservices.delivery.reproduction.util.ReproductionStandardOptions;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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
public class ReproductionServiceImpl extends RequestServiceImpl implements ReproductionService {
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
    private BeanFactory bf;

    @Autowired
    @Qualifier("myCustomProperties")
    private Properties properties;

    private Logger log = Logger.getLogger(getClass());

    /**
     * Remove a Reproduction from the database.
     *
     * @param obj Reproduction to remove.
     */
    public void removeReproduction(Reproduction reproduction) {
        // Set all holdings linked  to this reproduction back to AVAILABLE if this is a PENDING/ACTIVE reproduction.
        // Note that we are in a transaction here, so it does not matter the records are still linked
        // to the reproduction when setting them to available.
        if ((reproduction.getStatus() == Reproduction.Status.PENDING) ||
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
     * Get a criteria builder for querying HoldingReproductions.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getHoldingReproductionCriteriaBuilder() {
        return holdingReproductionDAO.getCriteriaBuilder();
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
        initHoldingReproductions(newReproduction);

        // Move status forward if all order details are known already
        if (newReproduction.hasOrderDetails()) {
            newReproduction.updateStatusAndAssociatedHoldingStatus(Reproduction.Status.HAS_ORDER_DETAILS);
        }

        // Add or save the record when no errors are present.
        if (!result.hasErrors()) {
            if (oldReproduction == null) {
                newReproduction.setCreationDate(new Date());
                addReproduction(newReproduction);
            } else {
                oldReproduction.mergeWith(newReproduction);
                saveReproduction(oldReproduction);
            }
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
        if (!r.hasOrderDetails())
            throw new IncompleteOrderDetailsException();

        // Check if we maybe already created an order before
        Order order = r.getOrder();
        if (order != null)
            return order;

        // No need to create an order if the order is for free
        if (r.isForFree()) {
            r.updateStatusAndAssociatedHoldingStatus(Reproduction.Status.PAYED);
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
            r.updateStatusAndAssociatedHoldingStatus(Reproduction.Status.ORDER_CREATED);
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
    @Async
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
     */
    public void initHoldingReproductions(Reproduction reproduction) {
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            ReproductionStandardOption standardOption = hr.getStandardOption();

            // Determine if we can specify the price and delivery time, but have not done so yet
            if ((standardOption != null) && ((hr.getPrice() == null) || (hr.getDeliveryTime() == null))) {
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
     * Add a Reproduction to the database.
     *
     * @param obj Reproduction to add.
     */
    public void addReproduction(Reproduction obj) {
        // Make sure the holdings get set to the correct status.
        obj.updateStatusAndAssociatedHoldingStatus(obj.getStatus());

        // Add to the database
        reproductionDAO.add(obj);
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
     * Mark a specific item in a reproduction as seen, bumping it to the next status.
     *
     * @param h Holding to bump.
     * @param r Reproduction to change status for.
     */
    public void markItem(Reproduction r, Holding h) {
        // Ignore old reproductions
        if (r == null)
            return;

        // Change holding status
        switch (h.getStatus()) {
            case RESERVED:
                h.setStatus(Holding.Status.IN_USE);
                break;
            case IN_USE:
                h.setStatus(Holding.Status.RETURNED);
                break;
            case RETURNED:
                h.setStatus(Holding.Status.AVAILABLE);
                break;
        }

        // Change reproduction status
        boolean complete = true;
        boolean reserved = true;
        for (HoldingReproduction hr : r.getHoldingReproductions()) {
            Holding holding = hr.getHolding();
            /* TODO: if (holding.getStatus() != Holding.Status.AVAILABLE) {
                complete = false;
            }
            if (holding.getStatus() != Holding.Status.RESERVED) {
                reserved = false;
            }*/
        }

        /*if (complete) {
            res.setStatus(Reservation.Status.COMPLETED);
        }
        else if (reserved) {
            res.setStatus(Reservation.Status.PENDING);
        }
        else {
            res.setStatus(Reservation.Status.ACTIVE);
        }*/

        saveReproduction(r);
    }

    /**
     * Returns the active reproduction with which this holding is associated.
     *
     * @param h The Holding to get the active reproduction of
     * @return The active reproduction, or null if no active reproduction exists
     */
    public Reproduction getActiveFor(Holding h) {
        return reproductionDAO.getActiveFor(h);
    }
}
