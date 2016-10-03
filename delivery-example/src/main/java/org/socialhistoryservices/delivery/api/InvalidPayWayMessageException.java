package org.socialhistoryservices.delivery.api;

/**
 * Thrown when an invalid (incorrect signature) PayWay message was recieved or when the action was unsuccesful.
 */
public class InvalidPayWayMessageException extends Exception {
    public InvalidPayWayMessageException(PayWayMessage message) {
        super("Received an invalid or unsuccesful PayWay message: " + message.toString());
    }

    public InvalidPayWayMessageException(PayWayMessage message, Throwable cause) {
        super("Received an invalid or unsuccesful PayWay message: " + message.toString(), cause);
    }
}
