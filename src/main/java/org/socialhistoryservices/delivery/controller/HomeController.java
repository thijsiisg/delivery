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

package org.socialhistoryservices.home.controller;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.annotation.Secured;

import javax.servlet.http.HttpServletRequest;
/**
 * Default controller when / is accessed.
 */
@RestController
public class HomeController {

    /**
     * Show a home overview page.
     * @param request The HTTP request to use.
     * @return The view to resolve.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @Secured("IS_AUTHENTICATED_REMEMBERED")
    public String list(HttpServletRequest request) {
        return "home";
    }
}
