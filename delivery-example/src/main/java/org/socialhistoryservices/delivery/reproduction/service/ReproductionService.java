package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;
import org.socialhistoryservices.delivery.reproduction.entity.Order;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.reproduction.entity.ReproductionStandardOption;
import org.socialhistoryservices.delivery.reproduction.util.ReproductionStandardOptions;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.InUseException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.BindingResult;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.awt.print.PrinterException;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface representing the service of the Reproduction package.
 */
public interface ReproductionService {

    /**
     * Remove a Reproduction from the database.
     *
     * @param obj Reproduction to remove.
     */
    public void removeReproduction(Reproduction reproduction);

    /**
     * Change the status of all holdings in a reproduction.
     *
     * @param reproduction Reproduction to change status for.
     * @param status       Status to change holdings to.
     */
    public void changeHoldingStatus(Reproduction reproduction, Holding.Status status);

    /**
     * Retrieve the Reproduction matching the given id.
     *
     * @param id Id of the Reproduction to retrieve.
     * @return The Reproduction matching the id.
     */
    public Reproduction getReproductionById(int id);

    /**
     * Retrieve the ReproductionStandardOption matching the given id.
     *
     * @param id Id of the ReproductionStandardOption to retrieve.
     * @return The ReproductionStandardOption matching the id.
     */
    public ReproductionStandardOption getReproductionStandardOptionById(int id);

    /**
     * List all HoldingReproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching HoldingReproductions.
     */
    public List<HoldingReproduction> listHoldingReproductions(CriteriaQuery<HoldingReproduction> q);

    /**
     * Returns all standard options for reproductions.
     *
     * @return A list with all standard options for reproductions.
     */
    public List<ReproductionStandardOption> getAllReproductionStandardOptions();

    /**
     * Get a criteria builder for querying HoldingReproductions.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getHoldingReproductionCriteriaBuilder();

    /**
     * Edit reproductions.
     *
     * @param newRes     The new reproduction to put in the database.
     * @param oldRes     The old reproduction in the database (if present).
     * @param result     The binding result object to put the validation errors in.
     * @param isCustomer Whether the customer is creating the reproduction.
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void createOrEdit(Reproduction newReproduction, Reproduction oldReproduction, BindingResult result,
                             boolean isCustomer) throws ClosedException, NoHoldingsException;

    /**
     * Creates an order for the given reproduction.
     *
     * @param r The reproduction.
     * @return The created and registered order. (Or null if the reproduction is for free)
     * @throws IncompleteOrderDetailsException   Thrown when not all holdings have an order ready.
     * @throws OrderRegistrationFailureException Thrown in case we failed to register the order in PayWay.
     */
    public Order createOrder(Reproduction r) throws IncompleteOrderDetailsException, OrderRegistrationFailureException;

    /**
     * Will refresh the given order by retrieving the order details from PayWay.
     * The API call is performed in a seperate thread and
     * a Future object is returned to see when and whether the refresh was succesful.
     *
     * @param order The order to refresh. The id must be set.
     * @return A Future object that will return the refreshed Order when succesful.
     */
    public Future<Order> refreshOrder(Order order);

    /**
     * Initializes the holding reproductions.
     * Determines if we can already state the price and delivery time for one or more chosen holdings.
     *
     * @param reproduction The reproduction.
     */
    public void initHoldingReproductions(Reproduction reproduction);

    /**
     * Validates and saves the standard reproduction options.
     *
     * @param standardOptions The standard reproduction options.
     * @param result          The binding result object to put the validation errors in.
     */
    public void editStandardOptions(ReproductionStandardOptions standardOptions, BindingResult result);

    /**
     * Add a Reproduction to the database.
     *
     * @param obj Reproduction to add.
     */
    public void addReproduction(Reproduction obj);

    /**
     * Save changes to a Reproduction in the database.
     *
     * @param obj Reproduction to save.
     */
    public void saveReproduction(Reproduction obj);

    /**
     * Prints a reproduction by using the default printer.
     *
     * @param reproduction The reproduction to print.
     * @param alwaysPrint  If set to true, already printed reproductions will also be printed.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed
     *                          (or ran out of paper for example).
     */
    public abstract void printReproduction(Reproduction reproduction, boolean alwaysPrint) throws PrinterException;

    /**
     * Print a reproduction if it was not printed yet.
     *
     * @param reproduction The reproduction to print.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed
     *                          (or ran out of paper for example).
     */
    public void printReproduction(Reproduction reproduction) throws PrinterException;

    /**
     * Mark a specific item in a reproduction as seen, bumping it to the next status.
     *
     * @param h Holding to bump.
     * @param r Reproduction to change status for.
     */
    public void markItem(Reproduction r, Holding h);

    /**
     * Returns the active reproduction with which this holding is associated.
     *
     * @param h The Holding to get the active reproduction of
     * @return The active reproduction, or null if no active reproduction exists
     */
    public Reproduction getActiveFor(Holding h);

    /**
     * Validate provided holding part of request.
     *
     * @param newReq     The new request containing holdings.
     * @param oldReq     The old request if applicable (or null).
     * @param checkInUse Whether to validate on holdings that are in use currently.
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws InUseException      Thrown when a new holding provided to be added
     *                             to the request is already in use by another request.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void validateHoldings(Request newReq, Request oldReq, boolean checkInUse)
            throws NoHoldingsException, InUseException, ClosedException;
}
