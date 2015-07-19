package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.request.service.RequestMailer;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Mailer to send information/confirmation mails dealing with reproductions.
 */
@Service
public class ReproductionMailer extends RequestMailer {

    /**
     * Mail a pending confirmation message to a customer who has just created a reproduction.
     *
     * @param reproduction The reproduction to extract mail details from.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    public void mailPending(Reproduction reproduction) throws MailException {
        assert reproduction.getStatus() == Reproduction.Status.WAITING_FOR_ORDER_DETAILS :
                "Can only mail pending when Reproduction status is WAITING_FOR_ORDER_DETAILS";

        String subject = getMessage("reproductionMail.pendingSubject", "Confirmation of reproduction");
        sendMail(reproduction, subject, "reproduction_pending.mail.ftl", getReproductionModel(reproduction),
                reproduction.getRequestLocale());
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

        String subject = getMessage("reproductionMail.offerReadySubject", "Confirmation of reproduction (offer ready)");
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
