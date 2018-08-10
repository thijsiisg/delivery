package org.socialhistoryservices.delivery.api;

/**
 * Thrown on failure to register a code with the IIIF service.
 */
public class IIIFServiceException extends Exception {
    public IIIFServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
