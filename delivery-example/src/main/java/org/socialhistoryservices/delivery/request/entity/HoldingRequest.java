package org.socialhistoryservices.delivery.request.entity;

import org.socialhistoryservices.delivery.record.entity.Holding;

public abstract class HoldingRequest {

    /**
     * Get the HoldingRequest's holding.
     *
     * @return the HoldingRequest's holding.
     */
    public abstract Holding getHolding();

    /**
     * Set the HoldingRequest's holding.
     *
     * @param h the HoldingRequest's holding.
     */
    public abstract void setHolding(Holding holding);

    /**
     * Get the HoldingRequest's request.
     *
     * @return the HoldingRequest's request.
     */
    public abstract Request getRequest();

    /**
     * Set the HoldingRequest's request.
     *
     * @param h the HoldingRequest's request.
     */
    public abstract void setRequest(Request request);

    /**
     * Get the comment on a specific holding in a request.
     *
     * @return The comment.
     */
    public abstract String getComment();

    /**
     * Set the comment on a specific holding in a request.
     *
     * @param val The value to set the comment to.
     */
    public abstract void setComment(String val);

    /**
     * Is the holding on hold for this request?
     *
     * @return Whether this holding is on hold for this request.
     */
    public abstract boolean isOnHold();

    /**
     * Set the holding on hold for this request?
     *
     * @param onHold Whether the holding must be placed on hold for this request.
     */
    public abstract void setOnHold(boolean onHold);

    /**
     * Is the holding completed for this request?
     *
     * @return Whether this holding is completed for this request.
     */
    public abstract boolean isCompleted();

    /**
     * Set the holding completed for this request?
     *
     * @param completed Whether the holding has been completed for this request.
     */
    public abstract void setCompleted(boolean completed);

    /**
     * Merge this HoldingRequest with another HoldingRequest.
     *
     * @param other Another HoldingRequest.
     */
    public void mergeWith(HoldingRequest other) {
        setComment(other.getComment());
        setOnHold(other.isOnHold());
        setCompleted(other.isCompleted());
    }
}
