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

import java.util.*;
import java.util.concurrent.Future;

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

        // Information about the current state
        Holding.Status oldStatus = h.getStatus();
        Request requestActiveBefore = requests.getActiveFor(h);
        Request requestsOnHoldBefore = requests.getOnHoldFor(h);

        // Determine the active request
        Reservation reservation = null;
        Reproduction reproduction = null;
        List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
        if (accessReservation && (requestActiveBefore instanceof Reservation))
            reservation = (Reservation) requestActiveBefore;
        if (accessReproduction && (requestActiveBefore instanceof Reproduction))
            reproduction = (Reproduction) requestActiveBefore;

        // Show the request corresponding to the scanned record
        if ((reservation != null) || (reproduction != null)) {
            // If the user may modify reservations, mark item for the active reservation
            if (reservation != null)
                futureList = reservations.markItem(reservation, h);

            // If the user may modify reproductions, mark item for the active reproduction
            if (reproduction != null)
                futureList = reproductions.markItem(reproduction, h);

            // Wait until holding status is completly updated
            boolean finished = false;
            while (!finished) {
                finished = true;
                for (Future<Boolean> future : futureList) {
                    if (!future.isDone())
                        finished = false;
                }
            }

            // Gather information about the new state
            Request requestActiveAfter = requests.getActiveFor(h);
            Request requestsOnHoldAfter = requests.getOnHoldFor(h);

            model.addAttribute("holding", h);
            model.addAttribute("oldStatus", oldStatus);

            model.addAttribute("reservation", reservation);
            model.addAttribute("reproduction", reproduction);

            model.addAttribute("requestActiveBefore", getRequestAsString(requestActiveBefore));
            model.addAttribute("requestsOnHoldBefore", getRequestAsString(requestsOnHoldBefore));
            model.addAttribute("requestActiveAfter", getRequestAsString(requestActiveAfter));
            model.addAttribute("requestsOnHoldAfter", getRequestAsString(requestsOnHoldAfter));

            // Also add information about the state of each of the reservation and/or reproduction holdings
            Set<Holding> holdings = new HashSet<Holding>();
            if ((reservation != null) && (reservation.getHoldings() != null))
                holdings.addAll(reservation.getHoldings());
            if ((reproduction != null) && (reproduction.getHoldings() != null))
                holdings.addAll(reproduction.getHoldings());
            model.addAttribute("holdingActiveRequests", getHoldingActiveRequests(holdings));

            return "request_scan";
        }

        model.addAttribute("error", "invalid");
        return "request_scan";
    }
}
