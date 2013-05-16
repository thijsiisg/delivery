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

package org.socialhistoryservices.delivery.user.service;

import org.socialhistoryservices.delivery.user.entity.Group;
import org.socialhistoryservices.delivery.user.entity.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the service of the user package.
 */
public interface UserService {
    /**
     * Add a User to the database.
     * @param obj User to add.
     */
    public void addUser(User obj);

    /**
     * Remove a User from the database.
     * @param obj User to remove.
     */
    public void removeUser(User obj);

    /**
     * Save changes to a User in the database.
     * @param obj User to save.
     */
    public void saveUser(User obj);

    /**
     * Retrieve the User matching the given Id.
     * @param id Id of the User to retrieve.
     * @return The User matching the Id.
     */
    public User getUserById(int id);

    /**
     * Retrieve the User matching the given username.
     * @param name Username of the User to retrieve.
     * @return The User matching the username.
     */
    public User getUserByName(String name);

    /**
     * Get a criteria builder for querying Users.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getUserCriteriaBuilder();

    /**
     * List all Users matching a built query.
     * @param query The query to match by.
     * @return A list of matching Users.
     */
    public List<User> listUsers(CriteriaQuery<User> query);

    /**
     * List all Users.
     * @return A list of Users.
     */
    public List<User> listUsers();

    /**
     * Get a single User matching a built query.
     * @param query The query to match by.
     * @return The matching User.
     */
    public User getUser(CriteriaQuery<User> query);

    /**
     * Add a Group to the database.
     * @param obj Group to add.
     */
    public void addGroup(Group obj);

    /**
     * Remove a Group from the database.
     * @param obj Group to remove.
     */
    public void removeGroup(Group obj);

    /**
     * Save changes to a Group in the database.
     * @param obj Group to save.
     */
    public void saveGroup(Group obj);

    /**
     * Retrieve the Group matching the given Id.
     * @param id Id of the Group to retrieve.
     * @return The Group matching the Id.
     */
    public Group getGroupById(int id);

    /**
     * Get a criteria builder for querying Groups.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getGroupCriteriaBuilder();

    /**
     * List all Groups matching a built query.
     * @param query The query to match by.
     * @return A list of matching Groups.
     */
    public List<Group> listGroups(CriteriaQuery<Group> query);

    /**
     * List all Groups.
     * @return A list of Groups.
     */
    public List<Group> listGroups();

    /**
     * Get a single Group matching a built query.
     * @param query The query to match by.
     * @return The matching Group.
     */
    public Group getGroup(CriteriaQuery<Group> query);

}
