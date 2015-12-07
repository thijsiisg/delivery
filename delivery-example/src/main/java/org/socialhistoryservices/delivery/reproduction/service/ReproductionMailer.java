package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
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
import java.util.Set;

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
     * @param reproduction     The reproduction to extract mail details from.
     * @param holdingsNotInSor The holdings which are not found in the SOR yet.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailPending(Reproduction reproduction, Set<Holding> holdingsNotInSor) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.WAITING_FOR_ORDER_DETAILS :
                "Can only mail pending when Reproduction status is WAITING_FOR_ORDER_DETAILS";

        Model model = getReproductionModel(reproduction);
        model.addAttribute("holdingsNotInSor", holdingsNotInSor);

        // Send an email to the customer
        String subjectCustomer = getMessage("reproductionMail.pendingSubjectCustomer", "Confirmation of reproduction");
        sendMail(reproduction, subjectCustomer, "reproduction_pending_customer.mail.ftl",
                model, reproduction.getRequestLocale());

        // Send an email to the reading room
        String subjectReadingRoom = getMessage("reproductionMail.pendingSubjectReadingRoom",
                "New reproduction waiting for offer");
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
                "Confirmation of reproduction - Your offer is ready");
        sendMail(reproduction, subject, "reproduction_offer_ready.mail.ftl", getReproductionModel(reproduction),
                reproduction.getRequestLocale());
    }

    /**
     * Mail payment confirmation message for a reproduction to the customer.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailPayed(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.PAYED :
                "Can only mail payed confirmation when Reproduction status is PAYED";

        String subject = getMessage("reproductionMail.payedSubject", "Confirmation of payment");
        sendMail(reproduction, subject, "reproduction_payed.mail.ftl", getReproductionModel(reproduction),
                reproduction.getRequestLocale());
    }

    /**
     * Mail repro which items of an active reproduction have been sent to the printer
     * and the SOR links where the other items are available for download.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailActive(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.ACTIVE :
                "Can only mail active when Reproduction status is ACTIVE";

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

        String subject = getMessage("reproductionMail.activeReproductionSubject", "New active reproduction");
        sendMail(subject, "reproduction_active.mail.ftl", model, ENGLISH_LOCALE);
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
