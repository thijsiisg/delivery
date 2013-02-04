
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

package org.iisg.delivery.record.dao;

import org.iisg.delivery.record.entity.Contact;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access Object of the Contact data
 * associated with a record.
 */
public interface ContactDAO {
    /**
     * Add a Contact to the database.
     * @param obj Contact to add.
     */
    public void add(Contact obj);

    /**
     * Remove a Contact from the database.
     * @param obj Contact to remove.
     */
    public void remove(Contact obj);

    /**
     * Save changes to a Contact in the database.
     * @param obj Contact to save.
     */
    public void save(Contact obj);

    /**
     * Retrieve the Contact matching the given Id.
     * @param id Id of the Contact to retrieve.
     * @return The Contact matching the Id.
     */
    public Contact getById(int id);

    /**
     * Get a criteria builder for querying Contacts.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all Contacts matching a built query.
     * @param query The query to match by.
     * @return A list of matching Contacts.
     */
    public List<Contact> list(CriteriaQuery<Contact> query);

    /**
     * Get a single Contact matching a built query.
     * @param query The query to match by.
     * @return The matching Contact.
     */
    public Contact get(CriteriaQuery<Contact> query);
}
