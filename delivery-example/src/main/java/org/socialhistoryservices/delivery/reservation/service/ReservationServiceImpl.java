/**
 * Copyright (C) 2013 International Institute of Social History
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.reservation.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.*;
import org.socialhistoryservices.delivery.request.service.ClosedException;
import org.socialhistoryservices.delivery.request.service.NoHoldingsException;
import org.socialhistoryservices.delivery.reservation.dao.HoldingReservationDAO;
import org.socialhistoryservices.delivery.reservation.dao.ReservationDAO;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation_;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Represents the service of the reservation package.
 */
@Service
@Transactional
public class ReservationServiceImpl extends AbstractRequestService implements ReservationService {
    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private HoldingReservationDAO holdingReservationDAO;

    @Autowired
    private BeanFactory bf;

    @Autowired
    @Qualifier("myCustomProperties")
    private Properties properties;

    private Logger log = Logger.getLogger(getClass());

    /**
     * Add a Reservation to the database.
     * @param obj Reservation to add.
     */
    public void addReservation(Reservation obj) {
        // Generate a new queue number if necessary
        if (obj.getQueueNo() == null) {
            Date now = new Date();
            Date today = (Date)obj.getDate().clone();
            today.setYear(now.getYear());
            today.setMonth(now.getMonth());
            today.setDate(now.getDate());

            if (today.equals(obj.getDate())) {
                // Get the last queue number today
                CriteriaBuilder builder = getReservationCriteriaBuilder();

                CriteriaQuery<Reservation> query = builder.createQuery(Reservation.class);
                Root<Reservation> root = query.from(Reservation.class);
                query.select(root);

                query.where(builder.notEqual(root.get(Reservation_.queueNo), 0));
                query.orderBy(builder.desc(root.get(Reservation_.creationDate)));

                Reservation result = getReservation(query);

                if (result == null
                        || result.getDate().getDate() != obj.getDate().getDate()
                        || result.getDate().getYear() != obj.getDate().getYear()
                        || result.getDate().getMonth() != obj.getDate().getMonth()
                   ) {
                    obj.setQueueNo(1);
                   }
                else {
                    obj.setQueueNo(result.getQueueNo()+1);
                }
            }
        }

        // Make sure the holdings get set to the correct status.
        updateStatusAndAssociatedHoldingStatus(obj, obj.getStatus());

        // Add to the database
        reservationDAO.add(obj);
    }

    /**
     * Remove a Reservation from the database.
     * @param obj Reservation to remove.
     */
    public void removeReservation(Reservation obj) {
        // Set all holdings linked  to this reservation back to AVAILABLE if
        // this is a PENDING/ACTIVE reservation.
        // Note that we are in a transaction here, so it does not matter the
        // records are still linked to the reservation when setting them to
        // available.
        if (obj.getStatus() != Reservation.Status.COMPLETED) {
            changeHoldingStatus(obj, Holding.Status.AVAILABLE);
        }
        reservationDAO.remove(obj);
    }

    /**
     * Change the status of all holdings in a reservation.
     * @param res Reservation to change status for.
     * @param status Status to change holdings to.
     */
    public void changeHoldingStatus(Reservation res, Holding.Status status) {
        super.changeHoldingStatus(res, status);
        saveReservation(res);
    }

    /**
     * Save changes to a Reservation in the database.
     * @param obj Reservation to save.
     */
    public void saveReservation(Reservation obj) {
        reservationDAO.save(obj);
    }

    /**
     * Retrieve the Reservation matching the given Id.
     * @param id Id of the Reservation to retrieve.
     * @return The Reservation matching the Id.
     */
    public Reservation getReservationById(int id) {
        return reservationDAO.getById(id);
    }

    /**
     * Get a criteria builder for querying Reservations.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReservationCriteriaBuilder() {
        return reservationDAO.getCriteriaBuilder();
    }

    /**
     * Get a criteria builder for querying HoldingReservations.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getHoldingReservationCriteriaBuilder() {
        return holdingReservationDAO.getCriteriaBuilder();
    }

    /**
     * List all Reservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching Reservations.
     */
    public List<Reservation> listReservations(CriteriaQuery<Reservation> q) {
        return reservationDAO.list(q);
    }

    /**
     * List all Tuples matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching Tuples.
     */
    public List<Tuple> listTuples(CriteriaQuery<Tuple> q) {
        return reservationDAO.listForTuple(q);
    }

    /**
     * List all HoldingReservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching HoldingReservations.
     */
    public List<HoldingReservation> listHoldingReservations(CriteriaQuery<HoldingReservation> q) {
            return holdingReservationDAO.list(q);
    }

    /**
     * Get a single Reservation matching a built query.
     * @param query The query to match by.
     * @return The matching Reservation.
     */
    public Reservation getReservation(CriteriaQuery<Reservation> query) {
        return reservationDAO.get(query);
    }

    /**
     * Mark a specific item in a reservation as seen, bumping it to the next status.
     * @param res Reservation to change status for.
     * @param h Holding to bump.
     */
    public void markItem(Reservation res, Holding h) {
        // Ignore old reservations
        if (res == null)
            return;

        Holding.Status newStatus = super.markItem(h);
        markReservation(res);

        requests.updateHoldingStatus(h, newStatus);
        saveReservation(res);
    }

    /**
     * Mark a reservation, bumping it to the next status.
     * @param res Reservation to change status for.
     */
    private void markReservation(Reservation res) {
        boolean complete = true;
        boolean reserved = true;
        for (HoldingReservation hr : res.getHoldingReservations()) {
            if (!hr.isCompleted()) {
                Holding holding = hr.getHolding();

                if (holding.getStatus() == Holding.Status.AVAILABLE) {
                    hr.setCompleted(true);
                }
                else {
                    complete = false;
                }

                if (holding.getStatus() != Holding.Status.RESERVED) {
                    reserved = false;
                }
            }
        }

        if (complete) {
            res.setStatus(Reservation.Status.COMPLETED);
        }
        else if (reserved) {
            res.setStatus(Reservation.Status.PENDING);
        }
        else {
            res.setStatus(Reservation.Status.ACTIVE);
        }
    }

    /**
     * Merge the other reservation's fields into this reservation.
     * @param reservation The reservation.
     * @param other The other reservation to merge with.
     */
    public void merge(Reservation reservation, Reservation other) {
        reservation.setDate(other.getDate());
        reservation.setReturnDate(other.getReturnDate());
        reservation.setPrinted(other.isPrinted());
        reservation.setSpecial(other.getSpecial());
        reservation.setVisitorName(other.getVisitorName());
        reservation.setVisitorEmail(other.getVisitorEmail());
        reservation.setComment(other.getComment());
        //setPermission(other.getPermission());
        //setQueueNo(other.getQueueNo());

        if (other.getHoldingReservations() == null) {
            for (HoldingReservation hr : reservation.getHoldingReservations()) {
                requests.updateHoldingStatus(hr.getHolding(), Holding.Status.AVAILABLE);
            }
            reservation.setHoldingReservations(new ArrayList<HoldingReservation>());
        }
        else {
            // Delete holdings that were not provided.
            deleteHoldingsNotInProvidedRequest(reservation, other);

            // Add/update provided.
            addOrUpdateHoldingsProvidedByRequest(reservation, other);
        }
        updateStatusAndAssociatedHoldingStatus(reservation, other.getStatus());
    }

    /**
     * Set the reservation status and update the associated holdings status
     * accordingly. Only updates status forward.
     * @param reservation The reservation.
     * @param status The reservation which changed status.
     */
    public void updateStatusAndAssociatedHoldingStatus(Reservation reservation, Reservation.Status status) {
        if (status.ordinal() <= reservation.getStatus().ordinal()) {
            return;
        }
        reservation.setStatus(status);
        Holding.Status hStatus;
        switch (status) {
            case PENDING:
                hStatus = Holding.Status.RESERVED;
                break;
            case ACTIVE:
                hStatus = Holding.Status.IN_USE;
                break;
            default:
                hStatus = Holding.Status.AVAILABLE;
                break;
        }
        for (HoldingReservation hr : reservation.getHoldingReservations()) {
            if (!hr.isCompleted()) {
                if (status == Reservation.Status.COMPLETED) {
                    hr.setCompleted(true);
                    requests.updateHoldingStatus(hr.getHolding(), hStatus, reservation);
                }
                else if (!hr.isOnHold()) {
                    requests.updateHoldingStatus(hr.getHolding(), hStatus, reservation);
                }
            }
        }
    }

    /**
     * Adds a HoldingRequest to the HoldingRequests assoicated with this request.
     *
     * @param holdingRequest The HoldingRequests to add.
     */
    protected void addToHoldingRequests(Request request, HoldingRequest holdingRequest) {
        Reservation reservation = (Reservation) request;
        HoldingReservation holdingReservation = (HoldingReservation) holdingRequest;

        holdingReservation.setReservation(reservation);
        reservation.getHoldingReservations().add(holdingReservation);
    }

    /**
     * Check whether there are any reservations made on the holding.
     * @param h Holding to check for reservations for.
     * @return Whether any reservations have been made including this holding.
     */
    public boolean hasReservations(Holding h) {
        return reservationDAO.hasReservations(h);
    }

    /**
     * Prints a reservation by using the default printer.
     * @param res The reservation to print.
     * @param alwaysPrint If set to true, already printed reservations will
     * also be printed.
     * @throws PrinterException Thrown when delivering the print job to the
     * printer failed. Does not say anything if the printer actually printed
     * (or ran out of paper for example).
     */
    public void printReservation(Reservation res, boolean alwaysPrint) throws PrinterException {
        try {
            List<RequestPrintable> requestPrintables = new ArrayList<RequestPrintable>();
            for (HoldingReservation hr : res.getHoldingReservations()) {
                ReservationPrintable rp = new ReservationPrintable(
                        hr, msgSource, (DateFormat) bf.getBean("dateFormat"), properties);
                requestPrintables.add(rp);
            }

            printRequest(res, requestPrintables, alwaysPrint);
        } catch (PrinterException e) {
            log.warn("Printing reservation failed", e);
            throw e;
        }

        saveReservation(res);
    }

    /**
     * Print a reservation if it was not printed yet.
     * @param res The reservation to print.
     * @throws PrinterException Thrown when delivering the print job to the
     * printer failed. Does not say anything if the printer actually printed
     * (or ran out of paper for example).
     */
    public void printReservation(Reservation res) throws PrinterException {
        printReservation(res, false);
    }

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
        InUseException, ClosedException, NoHoldingsException {

            // Validate the reservation.
            validateRequest(newRes, result);



            // Make sure a valid reservation date is provided (Only upon creation
            // because time dependent!).
            if (oldRes == null) {
                Date resDate = newRes.getDate();
                if (resDate != null && !resDate.equals(getFirstValidReservationDate
                            (resDate))) {
                    String msg =  msgSource.getMessage("validator.reservationDate", null,
                            "Invalid date", LocaleContextHolder.getLocale());
                    result.addError(new FieldError(result.getObjectName(), "date",
                                newRes.getDate(), false,
                                null, null, msg));
                            }
            }

            // If the return date is provided, it should be >= to the date of
            // visit.
            Date d = newRes.getDate();
            Date rd = newRes.getReturnDate();
            if (d != null && rd != null && rd.before(d)) {
                String msg =  msgSource.getMessage("validator.reservationReturnDate", null,
                        "Invalid date", LocaleContextHolder.getLocale());
                result.addError(new FieldError(result.getObjectName(),
                            "returnDate",
                            newRes.getDate(), false,
                            null, null, msg));
            }

            // Execute this method below the date check, or else the date will
            // not be checked if this method throws an exception; not displaying
            // the error immediately, but only when the holdings are valid instead.
            validateHoldingsAndAvailability(newRes, oldRes);

            // Add or save the record when no errors are present.
            if (!result.hasErrors()) {
                if (oldRes == null) {
                    newRes.setCreationDate(new Date());
                    addReservation(newRes);
                } else {
                    merge(oldRes, newRes);
                    saveReservation(oldRes);
                }
            }
        }

    /**
     * Get the first valid reservation date after or equal to from.
     * @param from The date to start from.
     * @return The first valid date, or null when maxDaysInAdvance was
     * exceeded.
     */
    public Date getFirstValidReservationDate(Date from) {
        Calendar fromCal = GregorianCalendar.getInstance();
        fromCal.setTime(from);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        format.setLenient(false);

        Calendar t = GregorianCalendar.getInstance();
        try {
            t.setTime(format.parse(properties.getProperty("prop_requestLatestTime")));
        } catch (ParseException e) {
            throw new RuntimeException("Invalid reservationLatestTime " +
                    "provided in config. Should be of format HH:mm");
        }

        Calendar firstPossibleCal = GregorianCalendar.getInstance();


        // Cannot reserve after "closing" time.
        if (firstPossibleCal.get(Calendar.HOUR_OF_DAY) > t.get(Calendar.HOUR_OF_DAY) ||
                (firstPossibleCal.get(Calendar.HOUR_OF_DAY) == t.get(Calendar
                                                                     .HOUR_OF_DAY)
                 &&
                 firstPossibleCal.get(Calendar.MINUTE)
                 >= t.get(Calendar.MINUTE))) {
            firstPossibleCal.add(Calendar.DAY_OF_YEAR, 1);
                 }
        // Cannot reserve in past (or after closing time).
        if (fromCal.get(Calendar.YEAR) < firstPossibleCal.get(Calendar.YEAR)
                || (
                    fromCal.get(Calendar.YEAR) == firstPossibleCal.get(Calendar
                        .YEAR) && fromCal.get(Calendar.DAY_OF_YEAR) < firstPossibleCal.get
                    (Calendar.DAY_OF_YEAR))) {
            fromCal = firstPossibleCal;
                    }

        // Check for weekends
        if (fromCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            fromCal.add(Calendar.DAY_OF_YEAR, 2);
        }
        if (fromCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            fromCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        Calendar maxCal = GregorianCalendar.getInstance();
        int maxDaysInAdvance = Integer.parseInt(properties.getProperty
                ("prop_reservationMaxDaysInAdvance"));
        maxCal.add(Calendar.DAY_OF_YEAR, maxDaysInAdvance);
        if (fromCal.get(Calendar.YEAR) > maxCal.get(Calendar.YEAR) || (fromCal
                    .get(Calendar.YEAR) == maxCal.get(Calendar.YEAR) && fromCal
                    .get(Calendar.DAY_OF_YEAR) > maxCal.get(Calendar.DAY_OF_YEAR)
                    )) {
            return null;
                    }

        return fromCal.getTime();
    }

    /**
     * Mark a request, bumping it to the next status.
     * @param r Request to change status for.
     */
    public void markRequest(Request r) {
        // Ignore old reservations
        if (!(r instanceof Reservation))
            return;

        Reservation reservation = (Reservation) r;
        markReservation(reservation);
        saveReservation(reservation);
    }

    /**
     * Returns the active reservation with which this holding is associated.
     * @param h The Holding to get the active reservation of
     * @param getAll Whether to return all active reservations (0)
     * or only those that are on hold (< 0) or those that are NOT on hold (> 0).
     * @return The active reservation, or null if no active reservation exists
     */
    public Reservation getActiveFor(Holding holding, int getAll) {
        return reservationDAO.getActiveFor(holding, getAll);
    }
}
