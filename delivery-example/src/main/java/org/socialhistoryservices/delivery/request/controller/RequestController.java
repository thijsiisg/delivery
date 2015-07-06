package org.socialhistoryservices.delivery.request.controller;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.reproduction.service.ReproductionService;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.socialhistoryservices.delivery.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
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
        SimpleGrantedAuthority reservationRole = new SimpleGrantedAuthority("ROLE_RESERVATION_MODIFY");
        SimpleGrantedAuthority reproductionRole = new SimpleGrantedAuthority("ROLE_REPRODUCTION_MODIFY");
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        Reservation reservation = null;
        Reproduction reproduction = null;
        Holding.Status oldStatus = h.getStatus();

        // If the user may modify reservations, see if a reservation for this holding is active
        if (authorities.contains(reservationRole)) {
            reservation = reservations.getActiveFor(h);
            if (reservation != null) {
                reservations.markItem(reservation, h);
            }
        }

        // If the user may modify reproductions, see if a reproduction for this holding is active
        if ((reservation == null) && authorities.contains(reproductionRole)) {
            reproduction = reproductions.getActiveFor(h);
            if (reproduction != null) {
                reproductions.markItem(reproduction, h);
            }
        }

        // Show the request corresponding to the scanned record.
        if ((reservation != null) || (reproduction != null)) {
            model.addAttribute("oldStatus", oldStatus);
            model.addAttribute("holding", h);
            model.addAttribute("reservation", reservation);
            model.addAttribute("reproduction", reproduction);
            return "request_scan";
        }

        model.addAttribute("error", "invalid");
        return "request_scan";
    }
}
