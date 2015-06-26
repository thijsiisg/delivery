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
     * Merge this HoldingRequest with another HoldingRequest.
     *
     * @param other Another HoldingRequest.
     */
    public void mergeWith(HoldingRequest other) {
        setComment(other.getComment());
    }
}
