/*
 * Copyright 2011 International Institute of Social History
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.socialhistoryservices.delivery.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;


/** Validates whether a reservation date is allowable. */
@Component
public class ReservationDateValidator implements
        ConstraintValidator<ValidReservationDate, Date> {

    @Autowired
    private ReservationService reservations;

    /**
     * Initialize this validator.
     * @param annotation The annotation to use.
     */
    public void initialize(ValidReservationDate annotation) {
    }

    /**
     * Check whether a date is valid or not.
     * @param dt The object to be considered as a date.
     * @param ctx The context.
     * @return Whether the date is valid or not.
     */
    public boolean isValid(Date dt, ConstraintValidatorContext ctx)  {
        // Nulls are considered invalid
        if (dt == null)
            return false;

        Date valid = reservations.getFirstValidReservationDate(dt);

        return valid != null && valid.equals(dt);
    }
}
