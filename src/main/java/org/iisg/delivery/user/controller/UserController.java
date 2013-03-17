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

import org.iisg.delivery.response.ErrorHandlingController;
import org.iisg.delivery.response.InvalidRequestException;
import org.iisg.delivery.user.entity.Group;
import org.iisg.delivery.user.entity.User;
import org.iisg.delivery.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller which handles all /user/* requests
 */
@Controller
@Transactional
@RequestMapping(value = "/user")
public class UserController extends ErrorHandlingController {

    @Autowired
    @Qualifier("userDetailsService")
    private UserService users;

    /**
     * Get the list of users to manage.
     * @param model The model to add attributes to.
     * @param request The HTTP request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Secured("ROLE_USER_MODIFY")
    public String list(Model model, HttpServletRequest request) {
        model.addAttribute("users", users.listUsers());
        model.addAttribute("groups", users.listGroups());
        return "user_management";
    }

    /**
     * Display a logout page.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/logout-success", method = RequestMethod.GET)
    public String logoutSuccess() {
        return "user_logout_success";
    }

    /**
     * Change the group a user is in.
     * @param user The user id.
     * @param groups The group ids.
     * @param model The model to add attributes to.
     * @param request The HTTP request.
     * @return The view to resolve.
     */
    @RequestMapping(value = "/", method = RequestMethod.POST,
                    params = "action=chgrp")
    @Secured("ROLE_USER_MODIFY")
    public String chgrp(
            @RequestParam int user,
            @RequestParam(required = false) int[] groups,
            Model model, HttpServletRequest request) {

        User userObj = users.getUserById(user);

        if (userObj == null) {
            throw new InvalidRequestException("Invalid user id specified.");
        }

        User currentUser = (User)SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (userObj.getId() == currentUser.getId()) {
            throw new InvalidRequestException("Cannot modify own user groups.");
        }


        userObj.getGroups().clear();

        if (groups != null) {
            for (int grpID : groups) {
                Group grp = users.getGroupById(grpID);
                userObj.getGroups().add(grp);
            }
        }

        users.saveUser(userObj);
        return "redirect:/user/";
    }
}
