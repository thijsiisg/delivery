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

package org.iisg.delivery.user.controller;

import org.iisg.delivery.user.entity.User;
import org.iisg.delivery.user.service.UserService;
import org.jasig.cas.client.util.XmlUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

/**
 * A Service Ticket Validator which automatically adds new users to the user
 * base.
 */
public class UserAddingServiceTicketValidator extends Cas20ServiceTicketValidator {


    private UserService userService;


    /**
     * Constructs a UserAdding service ticket validator.
     * @param casServerUrlPrefix The CAS server URL prefix.
     */
    public UserAddingServiceTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);

    }

    /**
     * Adds users to the user base if not existing yet.
     *
     * @param response the original response from the CAS server.
     * @param assertion the partially constructed assertion.
     * @throws TicketValidationException if there is a problem constructing the Assertion.
     */
    protected void customParseResponse(final String response,
                                       final Assertion assertion) throws TicketValidationException {
        // Get the user, this is always non-empty or the
        // Cas20ServiceTicketValidator had already thrown an exception.
        String principal = XmlUtils.getTextForElement(response, "user");

        // Add the user if needed.
        if (userService.getUserByName(principal) == null) {
            User u = new User();
            u.setUsername(principal);
            userService.addUser(u);
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
