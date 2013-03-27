/*
 * Copyright 2011 International Institute of Social History
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.socialhistoryservices.delivery.reservation.entity;

import org.socialhistoryservices.delivery.record.entity.Holding;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Reservation object representing a reservation that can be made on a set of
 * records.
 */
@Entity
@Table(name="holding_reservations")
public class HoldingReservation {


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

    /** The Reservation's name. */
    @Size(max=255)
    @Column(name="comment", nullable=true)
    private String comment;

    /**
     * Get the comment on a specific holding in a reservation.
     * @return The comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment on a specific holding in a reservation.
     * @param val The value to set the comment to.
     */
    public void setComment(String val) {
        comment = val;
    }

    /** The RecordPermission's permission. */
    @ManyToOne
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

    /** The HoldingReservation's holding. */
    @ManyToOne
    @JoinColumn(name="holding_id")
    private Holding holding;

    /**
     * Get the HoldingReservation's holding.
     * @return the HoldingReservation's holding.
     */
    public Holding getHolding() {
        return holding;
    }

    /**
     * Set the HoldingReservation's holding.
     * @param h the HoldingReservation's holding.
     */
    public void setHolding(Holding h) {
        this.holding = h;
    }

    public void mergeWith(HoldingReservation other) {
        setComment(other.getComment());
    }
}
