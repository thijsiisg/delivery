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

package org.socialhistoryservices.delivery.reservation.service;

import org.socialhistoryservices.delivery.request.service.RequestMailer;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Mailer to send confirmation mails when creating reservations.
 */
@Service
public class ReservationMailer extends RequestMailer {

    /**
     * Mail a confirmation message to a visitor who has just created a
     * reservation.
     * @param res The reservation to extract mail details from.
     * @throws org.springframework.mail.MailException Thrown when sending
     * mail somehow failed.
     */
    public void mailConfirmation(Reservation res) throws MailException {
        assert res.getStatus() == Reservation.Status.PENDING : "Can only mail" +
                " confirmation when Reservation status is PENDING";

        Model model = new ExtendedModelMap();
        model.addAttribute("reservation", res);

        String subject = getMessage("reservationMail.confirmationSubject", "Delivery: ");
        sendMail(res, subject, "mail/reservation_confirmation.mail.ftl", model, LocaleContextHolder.getLocale());
    }
}
