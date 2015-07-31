package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.request.service.RequestMailer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

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
                "Can only mail pending when Reproduction status is PAYED";

        String subject = getMessage("reproductionMail.payedSubject", "Confirmation of payment");
        sendMail(reproduction, subject, "reproduction_payed.mail.ftl", getReproductionModel(reproduction),
                reproduction.getRequestLocale());
    }

    /**
     * Mail the SOR links where the reproduction items are available for download. (For repro only!)
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailSorLinks(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.COMPLETED :
                "Can only mail SOR links when Reproduction status is COMPLETED";

        String subject = getMessage("reproductionMail.sorLinksSubject", "Reproduction SOR download links");
        sendMail(subject, "reproduction_sor_links.mail.ftl", getReproductionModel(reproduction), ENGLISH_LOCALE);
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
