package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.request.service.RequestMailer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Mailer to send information/confirmation mails dealing with reproductions.
 */
@Service
public class ReproductionMailer extends RequestMailer {
    private static final Locale ENGLISH_LOCALE = StringUtils.parseLocaleString("en");

    /**
     * Mail a pending confirmation message to a customer who has just created a reproduction.
     * Also mail an email to the reading room for filling out the blanks in the offer.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailPending(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.WAITING_FOR_ORDER_DETAILS :
                "Can only mail pending when Reproduction status is WAITING_FOR_ORDER_DETAILS";

        Model model = getReproductionModel(reproduction);

        // Send an email to the customer
        String subjectCustomer = getMessage("reproductionMail.pendingSubjectCustomer", "Confirmation of reproduction",
                reproduction.getRequestLocale());
        sendMail(reproduction, subjectCustomer, "reproduction_pending_customer.mail.ftl",
                model, reproduction.getRequestLocale());

        // Send an email to the reading room
        String subjectReadingRoom = getMessage("reproductionMail.pendingSubjectReadingRoom",
                "New reproduction waiting for offer", ENGLISH_LOCALE);
        sendMail(subjectReadingRoom, "reproduction_pending_readingroom.mail.ftl", model, ENGLISH_LOCALE);
    }

    /**
     * Mail an offer ready message for a reproduction to the customer.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailOfferReady(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.HAS_ORDER_DETAILS :
                "Can only mail order ready when Reproduction status is HAS_ORDER_DETAILS";

        String subject = getMessage("reproductionMail.offerReadySubject",
                "Confirmation of reproduction - Your offer is ready", reproduction.getRequestLocale());
        sendMail(reproduction, subject, "reproduction_offer_ready.mail.ftl", getReproductionModel(reproduction),
                reproduction.getRequestLocale());
    }

    /**
     * Mail payment confirmation message for a reproduction to the customer.
     * <p/>
     * Mail repro which items of an active reproduction have been sent to the printer
     * and the SOR links where the other items are available for download.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailPayedAndActive(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.ACTIVE :
                "Can only mail active and payed confirmation when Reproduction status is ACTIVE";

        List<HoldingReproduction> inSor = new ArrayList<HoldingReproduction>();
        List<HoldingReproduction> notInSor = new ArrayList<HoldingReproduction>();
        for (HoldingReproduction hr : reproduction.getHoldingReproductions()) {
            if (hr.isInSor())
                inSor.add(hr);
            else
                notInSor.add(hr);
        }

        Model model = getReproductionModel(reproduction);
        model.addAttribute("inSor", inSor);
        model.addAttribute("notInSor", notInSor);

        // First sent the customer a confirmation email
        String subjectCustomer = getMessage("reproductionMail.payedSubject", "Confirmation of payment",
                reproduction.getRequestLocale());
        sendMail(reproduction, subjectCustomer, "reproduction_payed.mail.ftl", model, reproduction.getRequestLocale());

        // Then sent the reading room / repro the confirmation
        String subjectRepro = getMessage("reproductionMail.activeReproductionSubject", "New active reproduction",
                ENGLISH_LOCALE);
        sendMail(subjectRepro, "reproduction_active.mail.ftl", model, ENGLISH_LOCALE);
    }

    /**
     * Mail the study room when a reproduction is cancelled.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailCancelled(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.CANCELLED :
                "Can only mail active when Reproduction status is CANCELLED";

        String subject = getMessage("reproductionMail.cancelledReproductionSubject", "Reproduction cancelled",
                ENGLISH_LOCALE);
        sendMail(subject, "reproduction_cancelled.mail.ftl", getReproductionModel(reproduction), ENGLISH_LOCALE);
    }

    /**
     * Returns a model with the reproduction.
     *
     * @param reproduction The reproduction.
     * @return A model with the reproduction.
     */
    private Model getReproductionModel(Reproduction reproduction) {
        Model model = new ExtendedModelMap();
        model.addAttribute("reproduction", reproduction);
        return model;
    }
}
