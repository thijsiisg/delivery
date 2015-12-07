package org.socialhistoryservices.delivery.request.entity;

import org.socialhistoryservices.delivery.record.entity.Holding;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class Request {

    /**
     * Returns the name of the person making the request.
     *
     * @return The name of the person.
     */
    public abstract String getName();

    /**
     * Returns the email address of the person making the request.
     *
     * @return The email address of the person.
     */
    public abstract String getEmail();

    /**
     * Set whether the request was printed or not.
     *
     * @param b True to consider the request to be printed at least once, false otherwise.
     */
    public abstract void setPrinted(boolean b);

    /**
     * Check if the Request (is considered) to be printed at least once.
     *
     * @return True if the Request was printed at least once, false otherwise.
     */
    public abstract boolean isPrinted();

    /**
     * Get the Request's creation date.
     *
     * @return the Request's creation date.
     */
    public abstract Date getCreationDate();

    /**
     * Set the Request's date.
     *
     * @param creationDate the Request's date.
     */
    public abstract void setCreationDate(Date creationDate);

    /**
     * Returns all holdings assoicated with this request.
     *
     * @return A list of holdings.
     */
    public abstract List<Holding> getHoldings();

    /**
     * Returns all HoldingRequests assoicated with this request.
     *
     * @return A list of HoldingRequests.
     */
    public abstract List<? extends HoldingRequest> getHoldingRequests();

    /**
     * Returns the status the holding should have to be placed 'on hold'.
     *
     * @return The holding status when the request is placed 'on hold'.
     */
    public abstract Holding.Status getOnHoldStatus();
}
