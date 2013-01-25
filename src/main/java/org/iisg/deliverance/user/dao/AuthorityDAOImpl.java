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

package org.iisg.deliverance.user.dao;

import org.iisg.deliverance.user.entity.Authority;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access Object of the user permissions.
 */
@Repository
public class AuthorityDAOImpl implements AuthorityDAO {
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
     * Add a Authority to the database.
     * @param obj Authority to add.
     */
    public void add(Authority obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a Authority from the database.
     * @param obj Authority to remove.
     */
    public void remove(Authority obj) {
        try {
            obj = entityManager.getReference(Authority.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
        entityManager.remove(obj);
    }

    /**
     * Save changes to a Authority in the database.
     * @param obj Authority to save.
     */
    public void save(Authority obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the Authority matching the given Id.
     * @param id Id of the Authority to retrieve.
     * @return The Authority matching the Id.
     */
    public Authority getById(int id) {
        return entityManager.find(Authority.class, id);
    }

    /**
     * Get a criteria builder for querying Authorities.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all Authorities matching a built query.
     * @param query The query to match by.
     * @return A list of matching authorities.
     */
    public List<Authority> list(CriteriaQuery<Authority> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single Authority matching a built query.
     * @param query The query to match by.
     * @return The matching Authority.
     */
    public Authority get(CriteriaQuery<Authority> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (Authority)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
