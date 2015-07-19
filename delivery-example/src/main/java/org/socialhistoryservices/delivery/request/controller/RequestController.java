package org.socialhistoryservices.delivery.request.controller;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.service.OnHoldException;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.reproduction.service.ReproductionService;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.GeneralRequestService;
import org.socialhistoryservices.delivery.request.service.RequestService;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.socialhistoryservices.delivery.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * Controller of the Request package, handles all /request/* requests.
 */
@Controller
@Transactional
@RequestMapping(value = "/request")
public class RequestController extends AbstractRequestController {
    @Autowired
    private ReservationService reservations;

    @Autowired
    private ReproductionService reproductions;

    @Autowired
    private GeneralRequestService requests;

    @Autowired
    private RecordService records;

    /**
     * Get the barcode scan page.
     *
     * @return The view to resolve.
     */
    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    @Secured({"ROLE_RESERVATION_MODIFY", "ROLE_REPRODUCTION_MODIFY"})
    public String scanBarcode() {
        return "request_scan";
    }

    /**
     * Process a scanned barcode.
     *
     * @param id    The scanned Record id.
     * @param model The model to add response attributes to.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/scan", method = RequestMethod.POST)
    @Secured({"ROLE_RESERVATION_MODIFY", "ROLE_REPRODUCTION_MODIFY"})
    public String scanBarcode(@RequestParam(required = false) String id, Model model) {
        // Obtain the scanned holding
        Holding h;
        try {
            int ID = Integer.parseInt(id);
            h = records.getHoldingById(ID);
        } catch (NumberFormatException ex) {
            h = null;
        }

        if (h == null) {
            model.addAttribute("error", "invalid");
            return "request_scan";
        }

        // Determine access rights
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        boolean accessReservation = authorities.contains(new SimpleGrantedAuthority("ROLE_RESERVATION_MODIFY"));
        boolean accessReproduction = authorities.contains(new SimpleGrantedAuthority("ROLE_REPRODUCTION_MODIFY"));

        // Determine the active request
        Reservation reservation = null;
        Reproduction reproduction = null;
        if (accessReservation)
            reservation = reservations.getActiveFor(h, 1);
        if (accessReproduction)
            reproduction = reproductions.getActiveFor(h, 1);

        boolean statusUpdated = false;
        Holding.Status oldStatus = h.getStatus();

        // If the user may modify reservations, mark item for the active reservation
        if (reservation != null) {
            reservations.markItem(reservation, h);
            statusUpdated = true;
        }

        // If the user may modify reproductions, mark item for the active reproduction
        if (reproduction != null) {
            reproductions.markItem(reproduction, h);
            statusUpdated = true;
        }

        // If there is no active request, then the holding is probably returned
        if ((reservation == null) && (reproduction == null) && (h.getStatus() == Holding.Status.RETURNED)) {
            requests.updateHoldingStatus(h, Holding.Status.AVAILABLE);
            records.saveHolding(h);
            statusUpdated = true;
        }

        // Show the request corresponding to the scanned record
        if (statusUpdated) {
            model.addAttribute("holding", h);
            model.addAttribute("reservation", reservation);
            model.addAttribute("reproduction", reproduction);
            model.addAttribute("oldStatus", oldStatus);
            model.addAttribute("reservationOnHold", null); // TODO
            model.addAttribute("reproductionOnHold", null); // TODO
            return "request_scan";
        }

        model.addAttribute("error", "invalid");
        return "request_scan";
    }
}
