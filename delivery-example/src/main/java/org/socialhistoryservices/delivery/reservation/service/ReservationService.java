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

package org.socialhistoryservices.delivery.reservation.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.InUseException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.springframework.validation.BindingResult;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.awt.print.PrinterException;
import java.util.Date;
import java.util.List;

/**
 * Interface representing the service of the reservation package.
 */
public interface ReservationService {
    /**
     * Add a Reservation to the database.
     * @param obj Reservation to add.
     */
    public void addReservation(Reservation obj);

    /**
     * Remove a Reservation from the database.
     * @param obj Reservation to remove.
     */
    public void removeReservation(Reservation obj);

    /**
     * Save changes to a Reservation in the database.
     * @param obj Reservation to save.
     */
    public void saveReservation(Reservation obj);

    /**
     * Retrieve the Reservation matching the given Id.
     * @param id Id of the Reservation to retrieve.
     * @return The Reservation matching the Id.
     */
    public Reservation getReservationById(int id);

    /**
     * Get a criteria builder for querying Reservations.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReservationCriteriaBuilder();

	/**
	 * Get a criteria builder for querying HoldingReservations.
	 * @return the CriteriaBuilder.
	 */
	public CriteriaBuilder getHoldingReservationCriteriaBuilder();

    /**
     * List all Reservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching Reservations.
     */
    public List<Reservation> listReservations(CriteriaQuery<Reservation> q);

	/**
	 * List all Tuples matching a built query.
	 * @param q The criteria query to execute
	 * @return A list of matching Tuples.
	 */
	public List<Tuple> listTuples(CriteriaQuery<Tuple> q);

	/**
	 * List all HoldingReservations matching a built query.
	 * @param q The criteria query to execute
	 * @return A list of matching HoldingReservations.
	 */
	public List<HoldingReservation> listHoldingReservations(CriteriaQuery<HoldingReservation> q);

    /**
     * Get a single Reservation matching a built query.
     * @param query The query to match by.
     * @return The matching Reservation.
     */
    public Reservation getReservation(CriteriaQuery<Reservation> query);

    /**
     * Change the status of all holdings in a reservation.
     * @param res Reservation to change status for.
     * @param status Status to change holdings to.
     */
    public void changeHoldingStatus(Reservation res, Holding.Status status);

    /**
     * Mark a specific item in a reservation as seen, bumping it to the next status.
     * @param res Reservation to change status for.
     * @param h Holding to bump.
     */
    public void markItem(Reservation res, Holding h);

    /**
     * Merge the other reservation's fields into this reservation.
     * @param reservation The reservation.
     * @param other The other reservation to merge with.
     */
    public void merge(Reservation reservation, Reservation other);

    /**
     * Set the reservation status and update the associated holdings status
     * accordingly. Only updates status forward.
     * @param reservation The reservation.
     * @param status The reservation which changed status.
     */
    public void updateStatusAndAssociatedHoldingStatus(Reservation reservation, Reservation.Status status);

    /**
     * Check whether there are any reservations made on the holding.
     * @param h Holding to check for reservations for.
     * @return Whether any reservations have been made including this holding.
     */
    public boolean hasReservations(Holding h);

    /**
     * Print a reservation if it was not printed yet.
     * @param res The reservation to print.
     * @throws PrinterException Thrown when delivering the print job to the
     * printer failed. Does not say anything if the printer actually printed
     * (or ran out of paper for example).
     */
    public void printReservation(Reservation res) throws PrinterException;

    /**
     * Prints a reservation by using the default printer.
     * @param res The reservation to print.
     * @param alwaysPrint If set to true, already printed reservations will
     * also be printed.
     * @throws PrinterException Thrown when delivering the print job to the
     * printer failed. Does not say anything if the printer actually printed
     * (or ran out of paper for example).
     */
    public void printReservation(Reservation res, boolean alwaysPrint) throws
            PrinterException;

    /**
     * Edit reservations.
     * @param newRes The new reservation to put in the database.
     * @param oldRes The old reservation in the database (if present).
     * @param result The binding result object to put the validation errors in.
     * @throws org.socialhistoryservices.delivery.request.service.ClosedException Thrown when a holding is provided which
     * references a record which is restrictionType=CLOSED.
     * @throws InUseException Thrown when a new holding provided to be added
     * to the reservation is already in use by another reservation.
     * @throws org.socialhistoryservices.delivery.request.service.NoHoldingsException Thrown when no holdings are provided.
     */
    public void createOrEdit(Reservation newRes, Reservation oldRes,
                                      BindingResult result) throws
            InUseException, ClosedException, NoHoldingsException;

    /**
     * Validate provided holding part of request.
     * @param newReq The new request containing holdings.
     * @param oldReq The old request if applicable (or null).
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws InUseException      Thrown when a new holding provided to be added
     *                             to the request is already in use by another request.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void validateHoldingsAndAvailability(Request newReq, Request oldReq)
            throws NoHoldingsException, InUseException, ClosedException;

    /**
     * Get the first valid reservation date after or equal to from.
     * @param from The date to start from.
     * @return The first valid date, or null when maxDaysInAdvance was
     * exceeded.
     */
    public Date getFirstValidReservationDate(Date from);

    /**
     * Returns the active reservation with which this holding is associated.
     * @param h The Holding to get the active reservation of.
     * @return The active reservation, or null if no active reservation exists.
     */
    public Reservation getActiveFor(Holding holding);
}
