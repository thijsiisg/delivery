package org.socialhistoryservices.delivery.reproduction.entity;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Reproduction object representing a reproduction that can be made on a set of records.
 */
@Entity
@Table(name = "holding_reproductions")
public class HoldingReproduction extends HoldingRequest {

    /**
     * The HoldingReproduction's id.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    /**
     * Get the HoldingReproduction's id.
     *
     * @return the HoldingReproduction's id.
     */
    public int getId() {
        return id;
    }

    @Digits(integer = 5, fraction = 2)
    @Column(name = "price")
    private BigDecimal price;

    /**
     * Get the price.
     *
     * @return the price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Set the price.
     *
     * @param price the price.
     */
    public void setPrice(BigDecimal price) {
        if (price != null)
            price = price.setScale(2);
        this.price = price;
    }

    @Min(0)
    @Column(name = "deliveryTime")
    private Integer deliveryTime;

    /**
     * Get the delivery time in days.
     *
     * @return the delivery time in days.
     */
    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Set the delivery time in days.
     *
     * @param deliveryTime the delivery time in days.
     */
    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    /**
     * The comment on a specific holding in a reproduction.
     */
    @Size(max = 255)
    @Column(name = "comment", nullable = true)
    private String comment;

    /**
     * Get the comment on a specific holding in a reproduction.
     *
     * @return The comment.
     */
    @Override
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment on a specific holding in a reproduction.
     *
     * @param val The value to set the comment to.
     */
    @Override
    public void setComment(String val) {
        comment = val;
    }

    /**
     * Is the holding on hold for this reproduction?
     */
    @Column(name = "on_hold", nullable = false)
    private boolean onHold = false;

    /**
     * Is the holding on hold for this reproduction?
     *
     * @return Whether this holding is on hold for this reproduction.
     */
    public boolean isOnHold() {
        return onHold;
    }

    /**
     * Set the holding on hold for this reproduction?
     *
     * @param onHold Whether the holding must be placed on hold for this reproduction.
     */
    public void setOnHold(boolean onHold) {
        this.onHold = onHold;
    }

    /**
     * Is the holding completed for this reproduction?
     */
    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    /**
     * Is the holding completed for this reproduction?
     *
     * @return Whether this holding is completed for this reproduction.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Set the holding completed for this reproduction?
     *
     * @param completed Whether the holding has been completed for this reproduction.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Is there already a reproduction in the SOR?
     */
    @Column(name = "inSor")
    private Boolean inSor;

    /**
     * Get whether there already is a reproduction in the SOR.
     *
     * @return Is there already a reproduction in the SOR?
     */
    public Boolean isInSor() {
        return inSor;
    }

    /**
     * Set whether there already is a reproduction in the SOR.
     *
     * @param inSor Is there already a reproduction in the SOR?
     */
    public void setInSor(boolean inSor) {
        this.inSor = inSor;
    }

    /**
     * The kind of custom reproduction the customer requires.
     */
    @Column(name = "customReproductionCustomer", nullable = true, columnDefinition = "TEXT")
    private String customReproductionCustomer;

    /**
     * Get the kind of custom reproduction the customer requires.
     *
     * @return the kind of custom reproduction the customer requires.
     */
    public String getCustomReproductionCustomer() {
        return customReproductionCustomer;
    }

    /**
     * Set the kind of custom reproduction the customer requires.
     *
     * @param customReproductionCustomer the kind of custom reproduction the customer requires.
     */
    public void setCustomReproductionCustomer(String customReproductionCustomer) {
        this.customReproductionCustomer = customReproductionCustomer;
    }

    /**
     * The kind of custom reproduction reply.
     */
    @Column(name = "customReproductionReply", nullable = true, columnDefinition = "TEXT")
    private String customReproductionReply;

    /**
     * Get the kind of custom reproduction reply.
     *
     * @return the kind of custom reproduction reply.
     */
    public String getCustomReproductionReply() {
        return customReproductionReply;
    }

    /**
     * Set the kind of custom reproduction reply.
     *
     * @param customReproductionReply the kind of custom reproduction reply.
     */
    public void setCustomReproductionReply(String customReproductionReply) {
        this.customReproductionReply = customReproductionReply;
    }

    /**
     * The HoldingReproduction's reproduction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reproduction_id")
    private Reproduction reproduction;

    /**
     * Get the HoldingReproduction's reproduction.
     *
     * @return the HoldingReproduction's reproduction.
     */
    public Reproduction getReproduction() {
        return reproduction;
    }

    /**
     * Set the HoldingReproduction's reproduction.
     *
     * @param reproduction the HoldingReproduction's reproduction.
     */
    public void setReproduction(Reproduction reproduction) {
        this.reproduction = reproduction;
    }

    /**
     * Get the HoldingRequest's request.
     *
     * @return the HoldingRequest's request.
     */
    @Override
    public Request getRequest() {
        return getReproduction();
    }

    /**
     * Set the HoldingRequest's request.
     *
     * @param request
     */
    @Override
    public void setRequest(Request request) {
        setReproduction((Reproduction) request);
    }

    /**
     * The HoldingReproduction's holding.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holding_id")
    private Holding holding;

    /**
     * Get the HoldingReproduction's holding.
     *
     * @return the HoldingReproduction's holding.
     */
    @Override
    public Holding getHolding() {
        return holding;
    }

    /**
     * Set the HoldingReproduction's holding.
     *
     * @param h the HoldingReproduction's holding.
     */
    @Override
    public void setHolding(Holding h) {
        this.holding = h;
    }

    /**
     * The HoldingReproduction's standard option (if chosen).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reproduction_standard_option_id")
    private ReproductionStandardOption standardOption;

    public ReproductionStandardOption getStandardOption() {
        return standardOption;
    }

    public void setStandardOption(ReproductionStandardOption standardOption) {
        this.standardOption = standardOption;
    }

    /**
     * Whether the price and delivery time is determined and thus has all order details.
     *
     * @return Whether this holding contains all order details.
     */
    public boolean hasOrderDetails() {
        return ((getPrice() != null) && (getDeliveryTime() != null));
    }

    /**
     * Merge this HoldingRequest with another HoldingRequest.
     *
     * @param other Another HoldingRequest.
     */
    public void mergeWith(HoldingRequest other) {
        super.mergeWith(other);
        if (other instanceof HoldingReproduction) {
            HoldingReproduction otherHr = (HoldingReproduction) other;

            if ((getStandardOption() != otherHr.getStandardOption()) || (otherHr.getStandardOption() == null)) {
                setStandardOption(otherHr.getStandardOption());
                setPrice(otherHr.getPrice());
                setDeliveryTime(otherHr.getDeliveryTime());
            }

            if (otherHr.getStandardOption() == null) {
                setCustomReproductionCustomer(otherHr.getCustomReproductionCustomer());
                setCustomReproductionReply(otherHr.getCustomReproductionReply());
            }
        }
    }
}
