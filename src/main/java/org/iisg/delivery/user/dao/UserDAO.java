
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

package org.iisg.delivery.user.dao;

import org.iisg.delivery.user.entity.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access Object of the user entities.
 */
public interface UserDAO {
    /**
     * Add a User to the database.
     * @param obj User to add.
     */
    public void add(User obj);

    /**
     * Remove a User from the database.
     * @param obj User to remove.
     */
    public void remove(User obj);

    /**
     * Save changes to a User in the database.
     * @param obj User to save.
     */
    public void save(User obj);

    /**
     * Retrieve the User matching the given Id.
     * @param id Id of the User to retrieve.
     * @return The User matching the Id.
     */
    public User getById(int id);

    /**
     * Get a criteria builder for querying Users.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all Users matching a built query.
     * @param query The query to match by.
     * @return A list of matching Users.
     */
    public List<User> list(CriteriaQuery<User> query);

    /**
     * Get a single User matching a built query.
     * @param query The query to match by.
     * @return The matching User.
     */
    public User get(CriteriaQuery<User> query);
}
