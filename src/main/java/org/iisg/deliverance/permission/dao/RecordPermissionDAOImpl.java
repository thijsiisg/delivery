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

package org.iisg.deliverance.permission.dao;

import org.iisg.deliverance.permission.entity.RecordPermission;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface to represent the Data Access Object of permissions on individual
 * records.
 */
@Repository
public class RecordPermissionDAOImpl implements RecordPermissionDAO {
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
     * Add a RecordPermission to the database.
     * @param obj RecordPermission to add.
     */
    public void add(RecordPermission obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a RecordPermission from the database.
     * @param obj RecordPermission to remove.
     */
    public void remove(RecordPermission obj) {
        try {
            obj = entityManager.getReference(RecordPermission.class,
                    obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

    /**
     * Save changes to a RecordPermission in the database.
     * @param obj RecordPermission to save.
     */
    public void save(RecordPermission obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the RecordPermission matching the given Id.
     * @param id Id of the RecordPermission to retrieve.
     * @return The RecordPermission matching the Id.
     */
    public RecordPermission getById(int id) {
        return entityManager.find(RecordPermission.class, id);
    }

    /**
     * Get a criteria builder for querying RecordPermissions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all RecordPermissions matching a built query.
     * @param query The query to match by.
     * @return A list of matching RecordPermissions.
     */
    public List<RecordPermission> list(CriteriaQuery<RecordPermission> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single RecordPermission matching a built query.
     * @param query The query to match by.
     * @return The matching RecordPermission.
     */
    public RecordPermission get(CriteriaQuery<RecordPermission> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (RecordPermission)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
