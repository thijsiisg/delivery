/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.socialhistoryservices.delivery.permission.service;

import org.socialhistoryservices.delivery.Mailer;
import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Locale;

/**
 * Mailer to send confirmation mails when creating reservations.
 */
@Service
public class PermissionMailer extends Mailer {

    /**
     * Mails a requester when the permission request has been approved or
     * refused. If all the records were denied access, the requester will
     * receive a mail without an access code. Else a mail with an access code
     * will be sent.
     * @param pm The permission to use for composing the mail.
     * @throws org.springframework.mail.MailException Thrown when sending
     * mail somehow failed.
     */
    public void mailCode(Permission pm) throws MailException {
        // Do not mail when mail is disabled.
        if (!deliveryProperties.isMailEnabled()) {
            return;
        }

        Model model = new ExtendedModelMap();
        model.addAttribute("permission", pm);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(deliveryProperties.getMailSystemAddress());
        msg.setTo(pm.getEmail());
        msg.setReplyTo(getMessage("iisg.email", ""));

        // Be sure the recipient will receive the message in their language,
        // instead of the language of the employee activating this mail
        // function.
        Locale rl = pm.getRequestLocale();
        model.addAttribute("locale", rl.toString());

        // Set content and title based on which template to send.
        if (isCodeEligible(pm)) {
            msg.setSubject(getMessage("permissionMail.approvedSubject",
                                 "Delivery: Permission Request Approved",
                                 rl));
            msg.setText(templateToString("mail/permission_approved.mail.ftl",
                        model, rl));

        }
        else {
            msg.setSubject(getMessage("permissionMail.refusedSubject",
                    "Delivery: Permission Request Refused", rl));
            msg.setText(templateToString("mail/permission_refused.mail.ftl",
                        model, rl));
        }
        mailSender.send(msg);
    }

    /**
     * Check whether to send a permission code or not. Only send one if there
     * is at least one record granted permission.
     * @param pm The permission to check.
     * @return Whether to send a permission code (true) or not (false).
     */
    private boolean isCodeEligible(Permission pm) {
        for (RecordPermission rp : pm.getRecordPermissions()) {
            if (rp.getGranted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mails a confirmation to the requester to tell the permission request
     * was successfully received.
     * @param pm The permission to use for composing the mail.
     * @throws org.springframework.mail.MailException Thrown when sending
     * mail somehow failed.
     */
    public void mailConfirmation(Permission pm) throws MailException {
        // Do not mail when mail is disabled.
        if (!deliveryProperties.isMailEnabled()) {
            return;
        }

        Model model = new ExtendedModelMap();
        model.addAttribute("permission", pm);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(deliveryProperties.getMailSystemAddress());
        msg.setTo(pm.getEmail());
        msg.setReplyTo(getMessage("iisg.email", ""));

        msg.setSubject(getMessage("permissionMail.confirmationSubject",
                             "Delivery: Permission Request Confirmation"));
        msg.setText(templateToString("mail/permission_confirmation.mail.ftl",
                    model));

        mailSender.send(msg);
    }

    /**
     * Mails the reading room to inform them of a new permission request.
     * @param pm The permission to use for composing the mail.
     * @throws org.springframework.mail.MailException Thrown when sending mail somehow failed.
     */
    public void mailReadingRoom(Permission pm) throws MailException {
        // Do not mail when mail is disabled.
        if (!Boolean.parseBoolean(properties.getProperty("prop_mailEnabled"))) {
            return;
        }

        Model model = new ExtendedModelMap();
        model.addAttribute("permission", pm);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(properties.getProperty("prop_mailSystemAddress"));
        msg.setTo(properties.getProperty("prop_mailReadingRoom"));

        msg.setSubject(getMessage("permissionMail.readingRoomSubject", "New permission request"));
        msg.setText(templateToString("permission_readingroom.mail.ftl", model));

        mailSender.send(msg);
    }
}
