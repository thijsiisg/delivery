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
     * Merge the other request's fields into this request.
     *
     * @param other The other request.
     */
    public abstract void mergeWith(Request other);

    /**
     * Adds a HoldingRequest to the HoldingRequests assoicated with this request.
     *
     * @param holdingRequest The HoldingRequests to add.
     */
    protected abstract void addToHoldingRequests(HoldingRequest holdingRequest);

    /**
     * Add/Update the holdings provided by the provided request.
     *
     * @param other The provided request.
     */
    protected void addOrUpdateHoldingsProvidedByRequest(Request other) {
        for (HoldingRequest hr : other.getHoldingRequests()) {
            Holding h = hr.getHolding();
            boolean has = false;
            for (HoldingRequest hr2 : getHoldingRequests()) {
                Holding h2 = hr2.getHolding();

                if (h.getSignature().equals(h2.getSignature()) && h.getRecord().equals(h2.getRecord())) {
                    has = true;
                    hr2.mergeWith(hr); // Update comment and such
                }
            }

            if (!has) {
                addToHoldingRequests(hr);
            }
        }
    }

    /**
     * Remove the holdings from this record, which are not in the other record.
     *
     * @param other The other record.
     */
    protected void deleteHoldingsNotInProvidedRequest(Request other) {
        Iterator<? extends HoldingRequest> it = getHoldingRequests().iterator();
        while (it.hasNext()) {
            HoldingRequest hr = it.next();
            Holding h = hr.getHolding();

            boolean has = false;
            for (HoldingRequest hr2 : other.getHoldingRequests()) {
                Holding h2 = hr2.getHolding();
                if (h.getSignature().equals(h2.getSignature()) && h.getRecord().equals(h2.getRecord())) {
                    has = true;
                    break;
                }
            }

            if (!has) {
                h.setStatus(Holding.Status.AVAILABLE);
                it.remove();
            }
        }
    }
}
