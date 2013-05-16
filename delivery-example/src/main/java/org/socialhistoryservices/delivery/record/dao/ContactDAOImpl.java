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

package org.socialhistoryservices.delivery.record.dao;

import org.socialhistoryservices.delivery.record.entity.Contact;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access Object of the Contact data
 * associated with a record.
 */
@Repository
public class ContactDAOImpl implements ContactDAO {
    private EntityManager entityManager;

    /**
     * Set the entity manager to use in this DAO, internal.
     * @param entityManager The manager.
     */
    @PersistenceContext
    private void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Add a Contact to the database.
     * @param obj Contact to add.
     */
    public void add(Contact obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a Contact from the database.
     * @param obj Contact to remove.
     */
    public void remove(Contact obj) {
        try {
            obj = entityManager.getReference(Contact.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

    /**
     * Save changes to a Contact in the database.
     * @param obj Contact to save.
     */
    public void save(Contact obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the Contact matching the given Id.
     * @param id Id of the Contact to retrieve.
     * @return The Contact matching the Id.
     */
    public Contact getById(int id) {
        return entityManager.find(Contact.class, id);
    }

    /**
     * Get a criteria builder for querying Contacts.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all Contacts matching a built query.
     * @param query The query to match by.
     * @return A list of matching Contacts.
     */
    public List<Contact> list(CriteriaQuery<Contact> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single Contact matching a built query.
     * @param query The query to match by.
     * @return The matching Contact.
     */
    public Contact get(CriteriaQuery<Contact> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (Contact)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
