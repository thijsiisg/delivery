package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.entity.Record;
import org.socialhistoryservices.delivery.request.entity.HoldingRequest;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the service of the request package to be used by the implementing services.
 */
public abstract class AbstractRequestService implements RequestService {
    @Autowired
    protected Validator validator;

    @Autowired
    protected MessageSource msgSource;

    @Autowired
    protected GeneralRequestService requests;

    /**
     * What should happen when the status of a holding is updated.
     *
     * @param holding       The holding. (With the status updated)
     * @param activeRequest The request which triggered the holding change.
     */
    public void onHoldingStatusUpdate(Holding holding, Request activeRequest) {
        return;
    }

    /**
     * What should happen when a holding is placed on hold.
     *
     * @param holding        The holding which has been placed on hold.
     * @param previousActive The request for which the holding was active, before being placed on hold.
     * @param nowActive      The request for which the holding is now active.
     */
    public void onHoldingOnHold(Holding holding, Request previousActive, Request nowActive) {
        return;
    }

    /**
     * Validate provided holding part of request.
     *
     * @param newReq The new request containing holdings.
     * @param oldReq The old request if applicable (or null).
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void validateHoldings(Request newReq, Request oldReq) throws NoHoldingsException, ClosedException {
        try {
            validateHoldings(newReq, oldReq, false);
        } catch (InUseException iue) {
            // Will not be thrown.
        }
    }

    /**
     * Validate provided holding part of request.
     *
     * @param newReq     The new request containing holdings.
     * @param oldReq     The old request if applicable (or null).
     * @param checkInUse Whether to validate on holdings that are in use currently.
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws InUseException      Thrown when a new holding provided to be added
     *                             to the request is already in use by another request.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    public void validateHoldingsAndAvailability(Request newReq, Request oldReq)
            throws NoHoldingsException, InUseException, ClosedException {
        validateHoldings(newReq, oldReq, true);
    }

    /**
     * Validate provided holding part of request.
     *
     * @param newReq     The new request containing holdings.
     * @param oldReq     The old request if applicable (or null).
     * @param checkInUse Whether to validate on holdings that are in use currently.
     * @throws ClosedException     Thrown when a holding is provided which
     *                             references a record which is restrictionType=CLOSED.
     * @throws InUseException      Thrown when a new holding provided to be added
     *                             to the request is already in use by another request.
     * @throws NoHoldingsException Thrown when no holdings are provided.
     */
    private void validateHoldings(Request newReq, Request oldReq, boolean checkInUse)
            throws NoHoldingsException, InUseException, ClosedException {
        if (newReq.getHoldingRequests() == null || newReq.getHoldingRequests().isEmpty()) {
            throw new NoHoldingsException();
        }

        // Check for in use holdings by other requests.
        // Check for CLOSED.
        // Do not check for usage restriction (This only needs to be checked in the visitor interface,
        // not when employees create a request for example, same for RESTRICTED on record).
        for (HoldingRequest hr : newReq.getHoldingRequests()) {
            boolean has = false;
            Holding h = hr.getHolding();

            if (oldReq != null) {
                for (HoldingRequest hr2 : oldReq.getHoldingRequests()) {
                    Holding h2 = hr2.getHolding();
                    if (h2.getRecord().equals(h.getRecord()) && h2.getSignature().equals(h.getSignature())) {
                        has = true;
                    }
                }
            }

            if (checkInUse && !has && h.getStatus() != Holding.Status.AVAILABLE) {
                throw new InUseException();
            }

            // Do not check already linked holdings for CLOSED.
            if (!has && h.getRecord().getRealRestrictionType() == Record.RestrictionType.CLOSED) {
                throw new ClosedException();
            }

            // Make sure the hr also knows the request.
            hr.setRequest(newReq);
        }
    }

    /**
     * Adds a HoldingRequest to the HoldingRequests assoicated with this request.
     *
     * @param holdingRequest The HoldingRequests to add.
     */
    protected abstract void addToHoldingRequests(Request request, HoldingRequest holdingRequest);

    /**
     * Add/Update the holdings provided by the provided request.
     *
     * @param other The provided request.
     */
    protected void addOrUpdateHoldingsProvidedByRequest(Request request, Request other) {
        for (HoldingRequest hr : other.getHoldingRequests()) {
            boolean has = false;

            for (HoldingRequest hr2 : request.getHoldingRequests()) {
                Holding h = hr.getHolding();
                Holding h2 = hr2.getHolding();

                if (h.getSignature().equals(h2.getSignature()) && h.getRecord().equals(h2.getRecord())) {
                    has = true;
                    hr2.mergeWith(hr); // Update comment and such
                }
            }

            if (!has) {
                addToHoldingRequests(request, hr);
            }
        }
    }

    /**
     * Remove the holdings from this record, which are not in the other record.
     *
     * @param other The other record.
     */
    protected void deleteHoldingsNotInProvidedRequest(Request request, Request other) {
        Iterator<? extends HoldingRequest> it = request.getHoldingRequests().iterator();
        while (it.hasNext()) {
            HoldingRequest hr = it.next();
            Holding h = hr.getHolding();

            boolean has = false;
            for (HoldingRequest hr2 : other.getHoldingRequests()) {
                Holding h2 = hr2.getHolding();
                if (h.getSignature().equals(h2.getSignature()) && h.getRecord().equals(h2.getRecord())) {
                    has = true;
                    break;
                }
            }

            if (!has) {
                it.remove();
                requests.updateHoldingStatus(h, Holding.Status.AVAILABLE);
            }
        }
    }

    /**
     * Change the status of all holdings in a request.
     *
     * @param request Request to change status for.
     * @param status  Status to change holdings to.
     */
    protected void changeHoldingStatus(Request request, Holding.Status status) {
        for (HoldingRequest hr : request.getHoldingRequests()) {
            Holding h = hr.getHolding();
            if (requests.getActiveFor(h) == request)
                requests.updateHoldingStatus(h, status, request);
        }
    }

    /**
     * Validate a request using the provided binding result to store errors.
     *
     * @param request The request.
     * @param result  The binding result.
     */
    protected void validateRequest(Request request, BindingResult result) {
        // Validate the request.
        validator.validate(request, result);

        // Validate associated HoldingRequests if present.
        // They also should have a request reference set in order to pass this check.
        int i = 0;
        for (HoldingRequest hr : request.getHoldingRequests()) {
            result.pushNestedPath("holdings" + request.getClass().getName() + "s[" + i + "]");
            validator.validate(hr, result);
            result.popNestedPath();

            if (hr.getHolding().getRecord().getExternalInfo().getMaterialType() ==
                    ExternalRecordInfo.MaterialType.SERIAL && hr.getComment() == null) {
                String msg = msgSource.getMessage("validator.serialYear", null, "Required",
                        LocaleContextHolder.getLocale());
                result.addError(new FieldError(result.getObjectName(), "holding" +
                        request.getClass().getSimpleName() + "s[" + i + "].comment", "", false, null, null, msg));
            }
            i++;
        }
    }

    /**
     * Prints printables by using the default printer.
     *
     * @param request           The request to print.
     * @param requestPrintables The printables to print.
     * @param alwaysPrint       If set to true, already printed requests will also be printed.
     * @throws PrinterException Thrown when delivering the print job to the printer failed.
     *                          Does not say anything if the printer actually printed (or ran out of paper for example).
     */
    protected void printRequest(Request request, List<RequestPrintable> requestPrintables, boolean alwaysPrint)
            throws PrinterException {
        // Check if the request should be printed or not.
        if (request.isPrinted() && !alwaysPrint) {
            return;
        }

        // TODO This is a hack, because it create multiple jobs printing one
        // page each instead of a job printing multiple pages.
        for (RequestPrintable requestPrintable : requestPrintables) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("delivery");
            // Autowiring does not seem to work in POJOs ?

            // Note: Use Book to make sure margins are correct.
            Book pBook = new Book();
            pBook.append(requestPrintable, new IISHPageFormat());

            job.setPageable(pBook);

            // Print the print job, throws PrinterException when something was wrong.
            job.print();
        }

        request.setPrinted(true);
    }

    /**
     * Mark a specific item in a request as seen, bumping it to the next status.
     *
     * @param h Holding to bump.
     */
    protected Holding.Status markItem(Holding h) {
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
        return h.getStatus();
    }
}
