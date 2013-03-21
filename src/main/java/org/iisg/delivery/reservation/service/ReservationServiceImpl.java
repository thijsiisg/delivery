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

package org.iisg.delivery.reservation.service;

import org.iisg.delivery.record.entity.ExternalRecordInfo;
import org.iisg.delivery.record.entity.Holding;
import org.iisg.delivery.record.entity.Record;
import org.iisg.delivery.record.service.RecordService;
import org.iisg.delivery.reservation.dao.ReservationDAO;
import org.iisg.delivery.reservation.entity.HoldingReservation;
import org.iisg.delivery.reservation.entity.Reservation;
import org.iisg.delivery.reservation.entity.Reservation_;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private RecordService recordService;

    @Autowired
    private BeanFactory bf;

    @Autowired
    private SimpleDateFormat df;

    @Autowired
    @Qualifier("myCustomProperties")
    private Properties properties;

    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource msgSource;


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
        obj.updateStatusAndAssociatedHoldingStatus(obj.getStatus());

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
        for (HoldingReservation hr : res.getHoldingReservations()) {
            hr.getHolding().setStatus(status);
        }
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
     * List all Reservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching Reservations.
     */
    public List<Reservation> listReservations(CriteriaQuery<Reservation> q) {
        return reservationDAO.list(q);
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
     * @param h Holding to bump.
     * @return A reservation in which this item was bumped, or null on failure
     */
    public Reservation markItem(Holding h) {
        Reservation res = reservationDAO.getActiveFor(h);

        // Ignore old reservations
        if (res == null)
            return null;

       // Change holding status
       switch (h.getStatus()) {
            case RESERVED:
                h.setStatus(Holding.Status.IN_USE);
            break;
            case IN_USE:
                h.setStatus(Holding.Status.RETURNED);
            break;
            case RETURNED:
                h.setStatus(Holding.Status.AVAILABLE);
            break;
        }

        // Change reservation status
        boolean complete = true;
        boolean reserved = true;
        for (HoldingReservation hr : res.getHoldingReservations()) {
            Holding holding = hr.getHolding();
            if (holding.getStatus() != Holding.Status.AVAILABLE) {
                complete = false;
            }
            if (holding.getStatus() != Holding.Status.RESERVED) {
                reserved = false;
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

        saveReservation(res);
        return res;
    }

    /**
     * Returns the active reservation with which this holding is associated.
     * @param h The Holding to get the active reservation of
     * @return The active reservation, or null if no active reservation exists
     */
    public Reservation getActiveFor(Holding h) {
        return reservationDAO.getActiveFor(h);
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

        // Check if the reservation should be printed or not.
        if (res.isPrinted() && !alwaysPrint) {
            return;
        }
        try {
            PrinterJob job = PrinterJob.getPrinterJob();

            // Autowiring does not seem to work in POJOs ?
            // Create a reservation printable
            ReservationPrintable rp = new ReservationPrintable(res,
                    msgSource,
                    (DateFormat)bf.getBean("dateFormat"), properties);

            job.setPrintable(rp, new IISHPageFormat());

            // Print the print job, throws PrinterException when something was
            // wrong.
            job.print();
        } catch (PrinterException e) {
            log.warn("Printing failed", e);
            throw e;
        }
        res.setPrinted(true);
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
     * @throws ClosedException Thrown when a holding is provided which
     * references a record which is restrictionType=CLOSED.
     * @throws InUseException Thrown when a new holding provided to be added
     * to the reservation is already in use by another reservation.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void createOrEdit(Reservation newRes, Reservation oldRes,
                                      BindingResult result) throws
            InUseException, ClosedException, NoHoldingsException {

        // Validate the reservation.
        validateReservation(newRes, result);



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
        validateHoldings(newRes, oldRes);

        // Add or save the record when no errors are present.
        if (!result.hasErrors()) {
            if (oldRes == null) {
                newRes.setCreationDate(new Date());
                addReservation(newRes);
            } else {
                oldRes.mergeWith(newRes);
                saveReservation(oldRes);
            }
        }
    }

    /**
     * Validate provided holding part of reservation.
     * @param newRes The new reservation containing holdings.
     * @param oldRes The old reservation if applicable (or null).
     * @throws ClosedException Thrown when a holding is provided which
     * references a record which is restrictionType=CLOSED.
     * @throws InUseException Thrown when a new holding provided to be added
     * to the reservation is already in use by another reservation.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void validateHoldings(Reservation newRes, Reservation oldRes) throws NoHoldingsException, InUseException, ClosedException {
        if (newRes.getHoldingReservations() == null || newRes.getHoldingReservations().isEmpty()) {
            throw new NoHoldingsException();
        }

        // Check for in use holdings by other reservations.
        // Check for CLOSED.
        // Do not check for usage restriction (This only needs to be checked
        // in the visitor interface, not when employees create a reservation
        // for example, same for RESTRICTED on record).
        for (HoldingReservation hr : newRes.getHoldingReservations()) {
            boolean has = false;
            Holding h = hr.getHolding();
            if (oldRes != null) {
                for (HoldingReservation hr2 : oldRes.getHoldingReservations()
                        ) {
                    Holding h2 = hr2.getHolding();
                    if (h2.getRecord().equals(h.getRecord()) && h2.getSignature().equals(h.getSignature())) {
                        has = true;
                    }
                }
            }
            if (!has && h.getStatus() != Holding.Status.AVAILABLE) {
                throw new InUseException();
            }
            // Do not check already linked holdings for CLOSED.
            if (!has && h.getRecord().getRealRestrictionType() == Record.RestrictionType.CLOSED) {
                throw new ClosedException();
            }
            // Make sure the hr also knows the reservation.
            hr.setReservation(newRes);

        }
    }

    /**
     * Validate a reservation using the provided binding result to store errors.
     * @param res The reservation.
     * @param result The binding result.
     */
    private void validateReservation(Reservation res, BindingResult result) {
        // Validate the reservation.
        validator.validate(res, result);


        // Validate associated holdingReservations if present. They also
        // should have a
        // reservation reference set in order to pass this check.
        int i = 0;
        for(HoldingReservation hr : res.getHoldingReservations()) {
            result.pushNestedPath("holdingsReservations["+i+"]");
            validator.validate(hr, result);
            result.popNestedPath();


            if (hr.getHolding().getRecord().getExternalInfo().getMaterialType()
                    == ExternalRecordInfo.MaterialType.SERIAL &&
                    hr.getComment() == null) {
                String msg =  msgSource.getMessage("validator.serialYear",
                        null,
                        "Required", LocaleContextHolder.getLocale());
                result.addError(new FieldError(result.getObjectName(),
                        "holdingReservations[" + i + "].comment",
                        "", false,
                        null, null, msg));
            }
            i++;
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
            t.setTime(format.parse(properties.getProperty("prop_reservationLatestTime")));
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

}
