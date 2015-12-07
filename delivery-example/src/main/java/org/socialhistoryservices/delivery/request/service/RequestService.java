package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.Request;

/**
 * Represents the service of the request package to be used by the implementing services.
 */
public interface RequestService {

    /**
     * Mark a request, bumping it to the next status.
     *
     * @param r Request to change status for.
     */
    public void markRequest(Request r);

    /**
     * Returns the active request with which this holding is associated.
     *
     * @param h      The Holding to get the active reservation of.
     * @param getAll Whether to return all active requests (0)
     *               or only those that are on hold (< 0) or those that are NOT on hold (> 0).
     * @return The active request, or null if no active request exists.
     */
    public Request getActiveFor(Holding holding, int getAll);

    /**
     * What should happen when the status of a holding is updated.
     *
     * @param holding       The holding. (With the status updated)
     * @param activeRequest The request which triggered the holding change.
     */
    public void onHoldingStatusUpdate(Holding holding, Request activeRequest);

    /**
     * What should happen when a holding is placed on hold.
     *
     * @param holding        The holding which has been placed on hold.
     * @param previousActive The request for which the holding was active, before being placed on hold.
     * @param nowActive      The request for which the holding is now active.
     */
    public void onHoldingOnHold(Holding holding, Request previousActive, Request nowActive);
}
