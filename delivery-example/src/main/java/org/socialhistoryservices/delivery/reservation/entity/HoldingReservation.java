/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.reservation.entity;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Reservation object representing a reservation that can be made on a set of
 * records.
 */
@Entity
@Table(name="holding_reservations")
public class HoldingReservation extends HoldingRequest {


    /** The Reservation's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the Reservation's id.
     * @return the Reservation's id.
     */
    public int getId() {
        return id;
    }

    /** Is the holding on hold for this reservation? */
    @Column(name="on_hold", nullable=false)
    private boolean onHold = false;

    /**
     * Is the holding on hold for this reservation?
     * @return Whether this holding is on hold for this reservation.
     */
    public boolean isOnHold() {
        return onHold;
    }

    /**
     * Set the holding on hold for this reservation?
     * @param onHold Whether the holding must be placed on hold for this reservation.
     */
    public void setOnHold(boolean onHold) {
        this.onHold = onHold;
    }

    /** Is the holding completed for this reservation? */
    @Column(name="completed", nullable=false)
    private boolean completed = false;

    /**
     * Is the holding completed for this reservation?
     * @return Whether this holding is completed for this reservation.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Set the holding completed for this reservation?
     * @param completed Whether the holding has been completed for this reservation.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /** The Reservation's name. */
    @Size(max=255)
    @Column(name="comment", nullable=true)
    private String comment;

    /**
     * Get the comment on a specific holding in a reservation.
     * @return The comment.
     */
    @Override
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment on a specific holding in a reservation.
     * @param val The value to set the comment to.
     */
    @Override
    public void setComment(String val) {
        comment = val;
    }

    /** The RecordPermission's permission. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

    /**
     * Get the HoldingReservation's reservation.
     * @return the HoldingReservation's reservation.
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Set the HoldingReservation's reservation.
     * @param res the HoldingReservation's reservation.
     */
    public void setReservation(Reservation res) {
        this.reservation = res;
    }

    /**
     * Get the HoldingRequest's request.
     *
     * @return the HoldingRequest's request.
     */
    @Override
    public Request getRequest() {
        return getReservation();
    }

    /**
     * Set the HoldingRequest's request.
     *
     * @param request
     */
    @Override
    public void setRequest(Request request) {
        setReservation((Reservation) request);
    }

    /** The HoldingReservation's holding. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="holding_id")
    private Holding holding;

    /**
     * Get the HoldingReservation's holding.
     * @return the HoldingReservation's holding.
     */
    @Override
    public Holding getHolding() {
        return holding;
    }

    /**
     * Set the HoldingReservation's holding.
     * @param h the HoldingReservation's holding.
     */
    @Override
    public void setHolding(Holding h) {
        this.holding = h;
    }
}
