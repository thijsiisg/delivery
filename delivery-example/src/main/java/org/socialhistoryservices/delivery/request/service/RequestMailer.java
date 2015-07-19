package org.socialhistoryservices.delivery.request.service;

import org.socialhistoryservices.delivery.Mailer;
import org.socialhistoryservices.delivery.request.entity.Request;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.Model;

import java.util.Locale;

/**
 * Mailer to send information/confirmation mails dealing with requests.
 */
public abstract class RequestMailer extends Mailer {

    /**
     * Send information/confirmation mails dealing with requests
     *
     * @param request      The request in question.
     * @param subject      The subject of the email.
     * @param templateName The name of the template to use.
     * @param model        The model.
     * @throws MailException Thrown when sending mail somehow failed.
     */
    protected void sendMail(Request request, String subject, String templateName, Model model, Locale locale)
            throws MailException {
        // Do not mail when mail is disabled.
        if (!Boolean.parseBoolean(properties.getProperty("prop_mailEnabled"))) {
            return;
        }

        model.addAttribute("locale", locale.toString());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(properties.getProperty("prop_mailSystemAddress"));
        msg.setTo(request.getEmail());
        msg.setReplyTo(getMessage("iisg.email", ""));

        msg.setSubject(subject);
        msg.setText(templateToString(templateName, model, locale));

        mailSender.send(msg);
    }
}
