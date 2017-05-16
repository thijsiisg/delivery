package org.socialhistoryservices.delivery.request.controller;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.service.RecordService;
import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.reproduction.service.ReproductionSearch;
import org.socialhistoryservices.delivery.reproduction.service.ReproductionService;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.socialhistoryservices.delivery.request.service.GeneralRequestService;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.socialhistoryservices.delivery.reservation.service.ReservationSearch;
import org.socialhistoryservices.delivery.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @PreAuthorize("hasAnyRole('ROLE_RESERVATION_MODIFY', 'ROLE_REPRODUCTION_MODIFY')")
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
    @PreAuthorize("hasAnyRole('ROLE_RESERVATION_MODIFY', 'ROLE_REPRODUCTION_MODIFY')")
    public String scanBarcode(@RequestParam(required = false) String id, Model model, HttpServletRequest req) {
        // Obtain the scanned holding
        Holding h;
        try {
            int ID = Integer.parseInt(id);
            // Obtain the scanned HoldingReservation/HoldingReproduction
            HoldingReservation holdingReservation = getCorrespondingHoldingReservation(ID, req);
            HoldingReproduction holdingReproduction = getCorrespondingHoldingReproduction(ID, req);
            // Check if either the HoldingReproduction or HoldingReservation is null. If so, variable h is null
            // If not, variable h is set to either one that is not null.
            if(holdingReproduction != null && !holdingReproduction.isCompleted()){
                h = holdingReproduction.getHolding();
            }else if(holdingReservation != null && !holdingReservation.isCompleted()){
                h = holdingReservation.getHolding();
            }else{
                h = null;
            }
        } catch (NumberFormatException ex) {
            h = null;
        }

        if (h == null) {
            model.addAttribute("error", "invalid");
            return "request_scan";
        }

        // Information about the current state
        Holding.Status oldStatus = h.getStatus();
        Request requestActive = requests.getActiveFor(h);

        // Determine the active request
        Reservation reservation = null;
        Reproduction reproduction = null;
        if (requestActive instanceof Reservation)
            reservation = (Reservation) requestActive;
        if (requestActive instanceof Reproduction)
            reproduction = (Reproduction) requestActive;

        // Show the request corresponding to the scanned record
        if ((reservation != null) || (reproduction != null)) {
            // If the user may modify reservations, mark item for the active reservation
            if (reservation != null)
                reservations.markItem(reservation, h);

            // If the user may modify reproductions, mark item for the active reproduction
            if (reproduction != null)
                reproductions.markItem(reproduction, h);

            model.addAttribute("holding", h);
            model.addAttribute("oldStatus", oldStatus);

            model.addAttribute("reservation", reservation);
            model.addAttribute("reproduction", reproduction);

            model.addAttribute("requestActive", getRequestAsString(requestActive));

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

    /**
     * Get the corresponding HoldingReproduction to the given ID from the barcode that has been scanned, if ID belongs to a reproduction, otherwise null.
     * @param ID The ID of the barcode being scanned
     * @param req The request parameters
     * @return the corresponding HoldingReproduction to the given ID, if ID belongs to a reproduction, otherwise null.
     */
    private HoldingReproduction getCorrespondingHoldingReproduction(int ID, HttpServletRequest req){
        HoldingReproduction holdingReproduction = null;
        // Creating the parameter map used to get a list of holdingreproductions
        Map<String, String[]> p = req.getParameterMap();
        // Getting a list of all the HoldingReproductions
        CriteriaBuilder cb = reproductions.getHoldingReproductionCriteriaBuilder();
        ReproductionSearch search = new ReproductionSearch(cb, p);
        CriteriaQuery<HoldingReproduction> cq = search.list();
        List<HoldingReproduction> holdingReproductionList = reproductions.listHoldingReproductions(cq);
        // Check whether a HoldingReproduction's id is the same as the given id, setting the HoldingReproduction variable
        for (HoldingReproduction hr : holdingReproductionList) {
            if(hr.getId() == ID){
                holdingReproduction = hr;
            }
        }
        return holdingReproduction;
    }

    /**
     * Get the corresponding HoldingReservation to the given ID from the barcode that has been scanned, if ID belongs to a reservation, otherwise null.
     * @param ID The ID of the barcode being scanned
     * @param req The request parameters
     * @return the corresponding HoldingReservation to the given ID, if ID belongs to a reservation, otherwise null.
     */
    private HoldingReservation getCorrespondingHoldingReservation(int ID, HttpServletRequest req){
        HoldingReservation holdingReservation = null;
        // Creating the parameter map used to get a list of holdingreservations
        Map<String, String[]> p = req.getParameterMap();
        // Getting a list of all the HoldingReservations
        CriteriaBuilder cb = reservations.getHoldingReservationCriteriaBuilder();
        ReservationSearch search = new ReservationSearch(cb, p);
        CriteriaQuery<HoldingReservation> cq = search.list();
        List<HoldingReservation> holdingReservationList = reservations.listHoldingReservations(cq);
        // Check whether a HoldingReservation's id is the same as the given id, setting the HoldingReservation variable
        for (HoldingReservation hr : holdingReservationList) {
            if(hr.getId() == ID){
                holdingReservation = hr;
            }
        }
        return holdingReservation;
    }
}
