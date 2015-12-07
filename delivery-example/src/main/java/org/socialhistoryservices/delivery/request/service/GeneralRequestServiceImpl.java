package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.service.OnHoldException;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Represents the service of the request package.
 */
@Service
@Transactional
public class GeneralRequestServiceImpl implements GeneralRequestService {
    @Autowired
    private Set<RequestService> requests;

    /**
     * Mark a request, bumping it to the next status.
     *
     * @param r Request to change status for.
     */
    public void markRequest(Request r) {
        for (RequestService requestService : requests)
            requestService.markRequest(r);
    }

    /**
     * Updates the status of a holding.
     *
     * @param holding The holding.
     * @param status  The new status.
     */
    public void updateHoldingStatus(Holding holding, Holding.Status status) {
        updateHoldingStatus(holding, status, null);
    }

    /**
     * Updates the status of a holding.
     *
     * @param holding       The holding.
     * @param status        The new status.
     * @param activeRequest The request which triggered the holding change.
     */
    public void updateHoldingStatus(Holding holding, Holding.Status status, Request activeRequest) {
        holding.setStatus(status);
        checkRequestHoldingsOnHold(holding);
        sentHoldingStatusUpdateEvent(holding, activeRequest);
    }

    /**
     * Mark a specific item in a request on hold.
     *
     * @param holding Holding in question.
     * @return The request that was updated.
     * @throws OnHoldException If the holding was already placed on hold for a request.
     */
    public Request markItemOnHold(Holding holding) throws OnHoldException {
        Request request = getOnHoldFor(holding);
        if (getOnHoldFor(holding) != null)
            throw new OnHoldException(request, holding);

        request = getActiveFor(holding);
        for (HoldingRequest hr : request.getHoldingRequests()) {
            Holding h = hr.getHolding();
            if ((holding.getId() == h.getId()) && (holding.getStatus() == request.getOnHoldStatus())) {
                hr.setOnHold(true);
                if (holding.getStatus() == Holding.Status.RESERVED)
                    holding.setStatus(Holding.Status.AVAILABLE);
                sentHoldingOnHoldEvent(h, request, getActiveFor(h));
            }
        }

        return request;
    }

    /**
     * Mark a specific item in a request as active after being on hold.
     *
     * @param holding Holding in question.
     * @return The request that was updated.
     * @throws OnHoldException If no holding was placed on hold for a request.
     */
    public Request markItemActive(Holding holding) throws OnHoldException {
        Request requestOnHold = getOnHoldFor(holding);
        if (requestOnHold == null)
            throw new OnHoldException(holding);

        Holding.Status onHoldStatus = requestOnHold.getOnHoldStatus();
        if (!(onHoldStatus == Holding.Status.RESERVED && holding.getStatus() == Holding.Status.AVAILABLE) &&
                !(onHoldStatus == Holding.Status.IN_USE && holding.getStatus() == Holding.Status.RETURNED))
            throw new OnHoldException(holding);

        // Make sure to mark the holding complete for the active request
        Request requestActive = getActiveFor(holding);
        if (requestActive != null) {
            for (HoldingRequest hr : requestActive.getHoldingRequests()) {
                if (hr.getHolding().getId() == holding.getId())
                    hr.setCompleted(true);
            }
            markRequest(requestActive);
        }

        // Now remove the flag 'on hold' and make sure the holding status is back to 'in use'
        for (HoldingRequest hr : requestOnHold.getHoldingRequests()) {
            Holding h = hr.getHolding();
            if (holding.getId() == h.getId()) {
                hr.setOnHold(false);
                h.setStatus(onHoldStatus);
            }
        }

        return requestOnHold;
    }

    /**
     * Get an active request relating to a specific Holding.
     *
     * @param holding Holding to find a request for.
     * @return The active request, null if none exist.
     */
    public Request getActiveFor(Holding holding) {
        return getActiveFor(holding, 1);
    }

    /**
     * Get a request relating to a specific Holding which is placed on hold.
     *
     * @param holding Holding to find a request for.
     * @return The request which is on hold, null if none exist.
     */
    public Request getOnHoldFor(Holding holding) {
        return getActiveFor(holding, -1);
    }

    /**
     * Get an active request relating to a specific Holding.
     *
     * @param holding Holding to find a request for.
     * @param getAll  Whether to return all active requests (0)
     *                or only those that are on hold (< 0) or those that are NOT on hold (> 0).
     * @return The active request, null if none exist.
     */
    public Request getActiveFor(Holding holding, int getAll) {
        Request activeRequest = null;
        for (RequestService requestService : requests) {
            Request request = requestService.getActiveFor(holding, getAll);
            // The request with the earliest creation date is always the actual active request
            if ((request != null) &&
                    ((activeRequest == null) || activeRequest.getCreationDate().after(request.getCreationDate()))) {
                activeRequest = request;
            }
        }
        return activeRequest;
    }

    /**
     * Lets all the request services know that the status of one of the holdings is updated.
     *
     * @param holding       The holding.
     * @param activeRequest The request which triggered the holding change.
     */
    private void sentHoldingStatusUpdateEvent(Holding holding, Request activeRequest) {
        for (RequestService requestService : requests) {
            requestService.onHoldingStatusUpdate(holding, activeRequest);
        }
    }

    /**
     * Lets all the request services know that a holding has been placed on hold.
     *
     * @param holding        The holding which has been placed on hold.
     * @param previousActive The request for which the holding was active, before being placed on hold.
     * @param nowActive      The request for which the holding is now active.
     */
    private void sentHoldingOnHoldEvent(Holding holding, Request previousActive, Request nowActive) {
        for (RequestService requestService : requests) {
            requestService.onHoldingOnHold(holding, previousActive, nowActive);
        }
    }

    /**
     * If the holding is now available or returned,
     * make sure that other requests with this holding on hold are now no longer on hold.
     *
     * @param holding The holding.
     */
    private void checkRequestHoldingsOnHold(Holding holding) {
        try {
            Holding.Status newStatus = holding.getStatus();
            if ((newStatus == Holding.Status.AVAILABLE) || (newStatus == Holding.Status.RETURNED))
                markItemActive(holding);
        } catch (OnHoldException e) {
            // No problem, we don't expect a holding to be on hold all the time
        }
    }
}
