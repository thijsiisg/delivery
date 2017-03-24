package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reproduction.entity.*;
import org.socialhistoryservices.delivery.reproduction.util.ReproductionStandardOptions;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.springframework.validation.BindingResult;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.awt.print.PrinterException;
import java.util.List;
import java.util.Map;
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
     * Retrieve the Order matching the given id.
     *
     * @param id Id of the Order to retrieve.
     * @return The Order matching the id.
     */
    public Order getOrderById(int id);

    /**
     * Retrieve the ReproductionStandardOption matching the given id.
     *
     * @param id Id of the ReproductionStandardOption to retrieve.
     * @return The ReproductionStandardOption matching the id.
     */
    public ReproductionStandardOption getReproductionStandardOptionById(int id);

    /**
     * Get the first Reproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return The first matching Reproduction.
     */
    public Reproduction getReproduction(CriteriaQuery<Reproduction> q);

    /**
     * List all Reproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching Reproductions.
     */
    public List<Reproduction> listReproductions(CriteriaQuery<Reproduction> q);

    /**
     * List all Tuples matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching Tuples.
     */
    public List<Tuple> listTuples(CriteriaQuery<Tuple> q);

    /**
     * List all HoldingReproduction matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching HoldingReproductions.
     */
    public List<HoldingReproduction> listHoldingReproductions(CriteriaQuery<HoldingReproduction> q);

    /**
     * List all HoldingReproduction matching a built query.
     *
     * @param q           The criteria query to execute
     * @param firstResult The first result to obtain
     * @param maxResults  The max number of results to obtain
     * @return A list of matching HoldingReproductions.
     */
    public List<HoldingReproduction> listHoldingReproductions(CriteriaQuery<HoldingReproduction> q,
                                                              int firstResult, int maxResults);

    /**
     * Count all HoldingReproductions matching a built query.
     * @param q The criteria query to execute
     * @return A count of matching HoldingReproductions.
     */
    public long countHoldingReproductions(CriteriaQuery<Long> q);

    /**
     * Returns all standard options for reproductions.
     *
     * @return A list with all standard options for reproductions.
     */
    public List<ReproductionStandardOption> getAllReproductionStandardOptions();

    /**
     * Returns all custom notes for reproductions.
     *
     * @return A list with all custom notes for reproductions.
     */
    public List<ReproductionCustomNote> getAllReproductionCustomNotes();

    /**
     * Returns all custom notes for reproductions.
     *
     * @return A map with all custom notes for reproductions by material type name.
     */
    public Map<String, ReproductionCustomNote> getAllReproductionCustomNotesAsMap();

    /**
     * Get a criteria builder for querying Reproductions.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReproductionCriteriaBuilder();

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
     * @throws ClosedException                Thrown when a holding is provided which
     *                                        references a record which is restrictionType=CLOSED.
     * @throws NoHoldingsException            Thrown when no holdings are provided.
     * @throws ClosedForReproductionException Thrown when a holding is provided which is closed for reproductions.
     */
    public void createOrEdit(Reproduction newReproduction, Reproduction oldReproduction, BindingResult result,
                             boolean isCustomer)
            throws ClosedException, NoHoldingsException, ClosedForReproductionException;

    /**
     * Validate provided holding part of reproduction.
     *
     * @param newRep The new reproduction containing holdings.
     * @param oldRep The old reproduction if applicable (or null).
     * @throws ClosedException                Thrown when a holding is provided which
     *                                        references a record which is restrictionType=CLOSED.
     * @throws NoHoldingsException            Thrown when no holdings are provided.
     * @throws ClosedForReproductionException Thrown when a holding is provided which is closed for reproductions.
     */
    public void validateReproductionHoldings(Reproduction newRep, Reproduction oldRep)
            throws NoHoldingsException, ClosedException, ClosedForReproductionException;

    /**
     * Whether the price and delivery time is determined for all holdings and
     * as a result the reproduction has all the order details.
     *
     * @param reproduction The reproduction.
     * @return Whether all holdings have order details.
     */
    public boolean hasOrderDetails(Reproduction reproduction);

    /**
     * Returns standard options for the given holding which are NOT available in the SOR.
     *
     * @param holding                     The holding.
     * @param reproductionStandardOptions The standard options to choose from.
     * @return A list of options that are currently not available online in the SOR.
     */
    public List<ReproductionStandardOption> getStandardOptionsNotInSor(Holding holding,
                                                                       List<ReproductionStandardOption> reproductionStandardOptions);

    /**
     * Returns standard options for the given holding which ARE available in the SOR,
     * and therefor does not accept the standard options.
     *
     * @param holding                     The holding.
     * @param reproductionStandardOptions The standard options to choose from.
     * @return A list of options that are currently not available online in the SOR.
     */
    public List<ReproductionStandardOption> getStandardOptionsInSorOnlyCustom(Holding holding,
                                                                              List<ReproductionStandardOption> reproductionStandardOptions);

    /**
     * Scheduled task to cancel all reproductions not payed within 5 days after the offer was ready.
     */
    public void checkPayedReproductions();

    /**
     * Scheduled task to send a reminder for all reproductions not paid within the time frame after the offer was ready.
     */
    public void checkReminderReproductions();

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
     * Prints holding reproductions by using the default printer.
     *
     * @param hrs         The holding reproductions to print.
     * @param alwaysPrint If set to true, already printed reproductions will also be printed.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed
     *                          (or ran out of paper for example).
     */
    public void printItems(List<HoldingReproduction> hrs, boolean alwaysPrint) throws PrinterException;

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
     * @param r Reproduction to change status for.
     * @param h Holding to bump.
     */
    public void markItem(Reproduction r, Holding h);

    /**
     * Merge the other reproduction's fields into this reproduction.
     *
     * @param reproduction The reproduction.
     * @param other        The other reproduction.
     */
    public void merge(Reproduction reproduction, Reproduction other);

    /**
     * Set the reproduction status and update the associated holdings status accordingly.
     * Only updates status forward.
     *
     * @param reproduction The reproduction.
     * @param status       The reproduction which changed status.
     */
    public void updateStatusAndAssociatedHoldingStatus(Reproduction reproduction, Reproduction.Status status);

    /**
     * Auto print all holdings of the given reproduction, if possible.
     * Run this in a separate thread, we do nothing on failure so in this case this is perfectly possible.
     *
     * @param reproduction The reproduction.
     */
    public void autoPrintReproduction(final Reproduction reproduction);

    /**
     * Returns the active reproduction with which this holding is associated.
     *
     * @param h The Holding to get the active reproduction of.
     * @return The active reproduction, or null if no active reproduction exists.
     */
    public Reproduction getActiveFor(Holding h);
}
