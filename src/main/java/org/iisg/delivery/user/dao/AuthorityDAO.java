
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

import org.iisg.delivery.user.entity.Authority;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access Object of the user permissions.
 */
public interface AuthorityDAO {
    /**
     * Add a Authority to the database.
     * @param obj Authority to add.
     */
    public void add(Authority obj);

    /**
     * Remove a Authority from the database.
     * @param obj Authority to remove.
     */
    public void remove(Authority obj);

    /**
     * Save changes to a Authority in the database.
     * @param obj Authority to save.
     */
    public void save(Authority obj);

    /**
     * Retrieve the Authority matching the given Id.
     * @param id Id of the Authority to retrieve.
     * @return The Authority matching the Id.
     */
    public Authority getById(int id);

    /**
     * Get a criteria builder for querying Authorities.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all Authorities matching a built query.
     * @param query The query to match by.
     * @return A list of matching authorities.
     */
    public List<Authority> list(CriteriaQuery<Authority> query);

    /**
     * Get a single Authority matching a built query.
     * @param query The query to match by.
     * @return The matching Authority.
     */
    public Authority get(CriteriaQuery<Authority> query);
}
