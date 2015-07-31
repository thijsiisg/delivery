package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.service.OnHoldException;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface representing the service of the RequestService package.
 */
public interface GeneralRequestService {

    /**
     * Mark a request, bumping it to the next status.
     *
     * @param r Request to change status for.
     */
    public void markRequest(Request r);

    /**
     * Updates the status of a holding.
     *
     * @param holding The holding.
     * @param status  The new status.
     * @return A list of futures for each request after the status update.
     */
    public List<Future<Boolean>> updateHoldingStatus(Holding holding, Holding.Status status);

    /**
     * Updates the status of a holding.
     *
     * @param holding       The holding.
     * @param status        The new status.
     * @param activeRequest The request which triggered the holding change.
     * @return A list of futures for each request after the status update.
     */
    public List<Future<Boolean>> updateHoldingStatus(Holding holding, Holding.Status status, Request activeRequest);

    /**
     * Go over the holdings of both requests to find differences in holding status.
     * For the differences, check holdings that are on hold and sent the status updated event.
     *
     * @param newRequest The new request.
     * @param oldRequest The old request.
     */
    public void updateHoldingStatusAfterMerge(Request newRequest, Request oldRequest);

    /**
     * Mark a specific item in a request on hold.
     *
     * @param holding Holding in question.
     * @return The request that was updated.
     * @throws OnHoldException If the holding was already placed on hold for a request.
     */
    public Request markItemOnHold(Holding holding) throws OnHoldException;

    /**
     * Mark a specific item in a request as active after being on hold.
     *
     * @param holding Holding in question.
     * @return The request that was updated.
     * @throws OnHoldException If no holding was placed on hold for a request.
     */
    public Request markItemActive(Holding holding) throws OnHoldException;

    /**
     * Get an active request relating to a specific Holding.
     *
     * @param holding Holding to find a request for.
     * @return The active request, null if none exist.
     */
    public Request getActiveFor(Holding holding);

    /**
     * Get a request relating to a specific Holding which is placed on hold.
     *
     * @param holding Holding to find a request for.
     * @return The request which is on hold, null if none exist.
     */
    public Request getOnHoldFor(Holding holding);

    /**
     * Get an active request relating to a specific Holding.
     *
     * @param holding Holding to find a request for.
     * @param getAll  Whether to return all active requests (0)
     *                or only those that are on hold (< 0) or those that are NOT on hold (> 0).
     * @return The active request, null if none exist.
     */
    public Request getActiveFor(Holding holding, int getAll);
}
