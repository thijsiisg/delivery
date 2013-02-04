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

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PermissionDateRangeValidator implements
        ConstraintValidator<ValidPermissionDateRange, Object> {

    @Autowired
    private SimpleDateFormat df;
    
    private String fromFieldName;
    private String toFieldName;

    /**
     * Initialize validator annotation field values.
     * @param constraintAnnotation The annotation to get the field names from.
     */
    public void initialize(ValidPermissionDateRange constraintAnnotation) {
        fromFieldName = constraintAnnotation.from();
        toFieldName = constraintAnnotation.to();
    }

    /**
     * Check whether the chosen date range is valid or not.
     * @param value The value to get the date range from.
     * @param context The context of the validator.
     * @return True if valid, false otherwise.
     */
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        

        BeanWrapperImpl wr = new BeanWrapperImpl(value);
        String from = (String)wr.getPropertyValue(fromFieldName);
        String to = (String)wr.getPropertyValue(toFieldName);
        if (from == null || to == null) {
            return false;
        }

        Date dateFrom, dateTo;
        try {


            dateFrom = df.parse(from);
            dateTo = df.parse(to);
        } catch (ParseException ex) {
            return false;
        }

        // From date should be before to date.
        return dateFrom.before(dateTo);
    }
}