package org.socialhistoryservices.delivery.reproduction.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.*;

/**
 * Reproduction object representing a Reproduction that can be made on a set of records.
 */
@Entity
@Table(name = "reproductions")
@Configurable
public class Reproduction extends Request {
    /**
     * Status of the reproduction.
     */
    public enum Status {
        WAITING_FOR_ORDER,
        ORDER_READY,
        CONFIRMED,
        PAYED,
        PENDING,
        ACTIVE,
        COMPLETED,
        DELIVERED
    }

    /**
     * The Reproduction's id.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    /**
     * Get the Reproduction's id.
     *
     * @return the Reproduction's id.
     */
    public int getId() {
        return id;
    }

    /**
     * The customers name.
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "customerName", nullable = false)
    private String customerName;

    /**
     * Get the customers name.
     *
     * @return the customers name.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Set the customers name.
     *
     * @param name the customers name.
     */
    public void setCustomerName(String name) {
        this.customerName = name;
    }

    /**
     * Returns the name of the person making the request.
     *
     * @return The name of the person.
     */
    @Override
    public String getName() {
        return getCustomerName();
    }

    /**
     * The customers email.
     */
    @NotBlank
    @Size(max = 255)
    @Email
    @Column(name = "customerEmail", nullable = false)
    private String customerEmail;

    /**
     * Get the customers email.
     *
     * @return the customers email.
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Set the customers email.
     *
     * @param email the customers email.
     */
    public void setCustomerEmail(String email) {
        this.customerEmail = email;
    }

    /**
     * Returns the email address of the person making the request.
     *
     * @return The email address of the person.
     */
    @Override
    public String getEmail() {
        return getCustomerEmail();
    }

    /**
     * The Reproduction's creation date.
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    /**
     * Get the Reproduction's creation date.
     *
     * @return the Reproduction's creation date.
     */
    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Set the Reproduction's date.
     *
     * @param creationDate the Reproduction's date.
     */
    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * The Reproduction's status.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    /**
     * Get the Reproduction's status.
     *
     * @return the Reproduction's status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the Reproduction's status.
     *
     * @param status the Reproduction's status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Whether the Reproduction has been printed or not.
     */
    @Column(name = "printed")
    private boolean printed;

    /**
     * Set whether the Reproduction was printed or not.
     *
     * @param b True to consider the Reproduction to be printed at least once, false otherwise.
     */
    @Override
    public void setPrinted(boolean b) {
        printed = b;
    }

    /**
     * Check if the Reproduction (is considered) to be printed at least once.
     *
     * @return True if the Reproduction was printed at least once,
     * false otherwise.
     */
    @Override
    public boolean isPrinted() {
        return printed;
    }

    /**
     * The Reproductions's comment.
     */
    @Size(max = 255)
    @Column(name = "comment")
    private String comment;

    /**
     * Get the comment on a Reproduction.
     *
     * @return The comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment on a Reproduction.
     *
     * @param val The value to set the comment to.
     */
    public void setComment(String val) {
        comment = val;
    }

    /**
     * A token for the user to access his/her own reproduction.
     */
    @NotBlank
    @Size(max = 36)
    @Column(name = "token", nullable = false)
    private String token;

    /**
     * Get the token.
     *
     * @return The token.
     */
    public String getToken() {
        return token;
    }

    @OneToMany(mappedBy = "reproduction", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<HoldingReproduction> holdingReproductions;

    public List<HoldingReproduction> getHoldingReproductions() {
        return holdingReproductions;
    }

    public void setHoldingReproductions(List<HoldingReproduction> holdingReproductions) {
        this.holdingReproductions = holdingReproductions;
    }

    /**
     * Returns all holdings assoicated with this request.
     *
     * @return A list of holdings.
     */
    @Override
    public List<Holding> getHoldings() {
        List<Holding> holdings = new ArrayList<Holding>();
        if (holdingReproductions != null) {
            for (HoldingReproduction holdingReproduction : holdingReproductions) {
                holdings.add(holdingReproduction.getHolding());
            }
            return holdings;
        }
        return null;
    }

    /**
     * Returns all HoldingRequests assoicated with this request.
     *
     * @return A list of HoldingRequests.
     */
    @Override
    public List<? extends HoldingRequest> getHoldingRequests() {
        return holdingReproductions;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name = "order_id")
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Set the reproduction status and update the associated holdings status accordingly.
     * Only updates status forward.
     * <p/>
     * TODO: Holding 'in use' state and relation with holdings in reservations.
     *
     * @param status The reservation which changed status.
     */
    public void updateStatusAndAssociatedHoldingStatus(Status status) {
        if (status.ordinal() < getStatus().ordinal()) {
            return;
        }

        setStatus(status);

        Holding.Status hStatus = (status == Status.ACTIVE) ? Holding.Status.IN_USE : Holding.Status.AVAILABLE;
        for (HoldingReproduction hr : getHoldingReproductions()) {
            hr.getHolding().setStatus(hStatus);
        }
    }

    /**
     * Whether the price and delivery time is determined for all holdings and
     * as a result this reproduction has all the order details.
     *
     * @return Whether all holdings have order details.
     */
    public boolean hasOrderDetails() {
        List<HoldingReproduction> hrs = getHoldingReproductions();
        if ((hrs == null) || hrs.isEmpty())
            return false;

        boolean hasOrderDetails = true;
        for (HoldingReproduction hr : hrs) {
            if (!hr.hasOrderDetails())
                hasOrderDetails = false;
        }

        return hasOrderDetails;
    }

    /**
     * Computes the total price of all holdings together.
     *
     * @return The total price for this reproduction.
     */
    public BigDecimal getTotalPrice() {
        BigDecimal price = new BigDecimal(0);
        for (HoldingReproduction hr : getHoldingReproductions()) {
            price = price.add(hr.getPrice());
        }

        return price.setScale(2);
    }

    /**
     * Whether this reproduction is for free.
     *
     * @return If this reproduction is for free.
     */
    public boolean isForFree() {
        return getTotalPrice().equals(BigDecimal.ZERO);
    }

    /**
     * Computes the estimated delivery time of all holdings together.
     *
     * @return The estimated delivery time for this reproduction.
     */
    public int getEstimatedDeliveryTime() {
        int deliveryTime = 0;
        for (HoldingReproduction hr : getHoldingReproductions()) {
            deliveryTime += hr.getDeliveryTime();
        }

        return deliveryTime;
    }

    /**
     * Returns whether this reproduction contains any custom reproduction requests.
     *
     * @return Wether this reproduction contains any custom reproduction requests.
     */
    public boolean containsCustomReproduction() {
        for (HoldingReproduction hr : getHoldingReproductions()) {
            if (hr.getStandardOption() == null)
                return true;
        }

        return false;
    }

    /**
     * Merge the other reproduction's fields into this reproduction.
     *
     * @param other The other reproduction.
     */
    @Override
    public void mergeWith(Request o) {
        setCustomerName(o.getName());
        setCustomerEmail(o.getEmail());
        setPrinted(o.isPrinted());

        if (o instanceof Reproduction) {
            Reproduction other = (Reproduction) o;
            setComment(other.getComment());

            if (other.getHoldingReproductions() == null) {
                for (HoldingReproduction hr : getHoldingReproductions()) {
                    hr.getHolding().setStatus(Holding.Status.AVAILABLE);
                }
                setHoldingReproductions(new ArrayList<HoldingReproduction>());
            } else {
                // Delete holdings that were not provided.
                deleteHoldingsNotInProvidedRequest(other);

                // Add/update provided.
                addOrUpdateHoldingsProvidedByRequest(other);
            }
            updateStatusAndAssociatedHoldingStatus(other.getStatus());
        }
    }

    /**
     * Adds a HoldingRequest to the HoldingRequests assoicated with this request.
     *
     * @param holdingRequest The HoldingRequests to add.
     */
    @Override
    protected void addToHoldingRequests(HoldingRequest holdingRequest) {
        HoldingReproduction holdingReproduction = (HoldingReproduction) holdingRequest;
        holdingReproduction.setReproduction(this);
        getHoldingReproductions().add(holdingReproduction);
    }

    /**
     * Set default data for Reproductions.
     */
    public Reproduction() {
        setStatus(Status.WAITING_FOR_ORDER);
        setCreationDate(new Date());
        setPrinted(false);
        holdingReproductions = new ArrayList<HoldingReproduction>();
        token = UUID.randomUUID().toString();
    }
}
