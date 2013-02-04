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

package org.iisg.delivery.permission.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/** Validates whether a reservation date is allowable. */
@Component
public class PermissionDateStringValidator implements
        ConstraintValidator<ValidPermissionDate, String> {
    @Autowired
    private SimpleDateFormat df;


    /**
     * Initializes the validator.
     * @param annotation The annotation parameters to use.
     */
    public void initialize(ValidPermissionDate annotation) {
    }

    /**
     * Check whether a date is valid or not.
     * @param object The object to be considered as a date.
     * @param ctx The context.
     * @return Whether the date is valid or not.
     */
    public boolean isValid(String object, ConstraintValidatorContext ctx)  {
        // Nulls are considered invalid
        if (object == null)
            return false;

        // Make sure we can parse it first
        Date dt;
        try {
            dt = df.parse(object);
        }
        catch (ParseException ex) {
            return false;
        }


        // Get the current day
        Date now = new Date();
        Date today = (Date)dt.clone();


        today.setYear(now.getYear());
        today.setMonth(now.getMonth());
        today.setDate(now.getDate());


        // If it's in the future or today, it is fine.
        if (dt.after(today) || dt.equals(today)) {
            return true;
        }

        // Cannot reserve in the past
        return false;
    }
}
