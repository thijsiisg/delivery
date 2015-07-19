package org.socialhistoryservices.delivery.record.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.Request;

/**
 * Indicates that there already was a request which was placed on hold for a specific holding
 * or that no request for which the holding was placed on hold could be found.
 */
public class OnHoldException extends Exception {

    public OnHoldException(Holding holding) {
        super(String.format(
                "No request for which the holding %s was placed on hold could be found.",
                holding
        ));
    }

    public OnHoldException(Request request, Holding holding) {
        super(String.format(
                "There already is a request, %s, which is placed on hold for holding %s.",
                request, holding
        ));
    }
}
