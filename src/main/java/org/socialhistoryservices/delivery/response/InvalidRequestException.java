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

package org.socialhistoryservices.delivery.response;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents an invalid request (HTTP status code 400).
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

    /**
     * Default constructor.
     */
    public InvalidRequestException() {
        super();
    }

    /**
     * Construct an invalid request exception.
     * @param msg The message to set.
     */
    public InvalidRequestException(String msg) {
        super(msg);
    }

    /**
     * Create an invalid request exception providing the errors (for use with
     * forms).
     * @param errors The errors to send.
     * @return The invalid request exception.
     */
    public static <T> InvalidRequestException create(BindingResult errors) {
        String msg = "";

        for (ObjectError err : errors.getAllErrors()) {
            if (err instanceof FieldError) {
                FieldError ferr = (FieldError)err;
                msg += "'" + ferr.getField() + "':" + ferr.getDefaultMessage()
                    + "\n";
            } else {
                msg += "'" + err.getObjectName() + "':" + err.getDefaultMessage()
                    + "\n";
            }

        }
        return new InvalidRequestException(msg);
    }
}
