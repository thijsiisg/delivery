package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.Request;

/**
 * Represents the service of the request package to be used by the implementing services.
 */
public interface RequestService {

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
     * @param holding The holding. (With the status updated)
     */
    public void onHoldingStatusUpdate(Holding holding);
}
