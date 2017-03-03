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

package org.socialhistoryservices.permission.dao;

import org.socialhistoryservices.permission.entity.Permission;
import org.socialhistoryservices.record.entity.Record;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access Object of Permissions (to request
 * Records which have a restricted status).
 */
@Repository
public class PermissionDAOImpl implements PermissionDAO {
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
     * Add a Permission to the database.
     * @param obj Permission to add.
     */
    public void add(Permission obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a Permission from the database.
     * @param obj Permission to remove.
     */
    public void remove(Permission obj) {
        try {
            obj = entityManager.getReference(Permission.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

    /**
     * Save changes to a Permission in the database.
     * @param obj Permission to save.
     */
    public void save(Permission obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the Permission matching the given Id.
     * @param id Id of the Permission to retrieve.
     * @return The Permission matching the Id.
     */
    public Permission getById(int id) {
        return entityManager.find(Permission.class, id);
    }

    /**
     * Get a criteria builder for querying Permissions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all Permissions matching a built query.
     * @param query The query to match by.
     * @return A list of matching Permissions.
     */
    public List<Permission> list(CriteriaQuery<Permission> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single Permission matching a built query.
     * @param query The query to match by.
     * @return The matching Permission.
     */
    public Permission get(CriteriaQuery<Permission> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (Permission)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Check whether there are any permission requests made on the record.
     * @param record Record to check for permission requests for.
     * @return Whether any permission requests have been made including this record.
     */
    public boolean hasPermissions(Record record) {
        String query = "select p from RecordPermission p join p.record r"+
                       " where r.id = :id";

        Query q = entityManager.createQuery(query);
        q.setParameter("id", record.getId());
        q.setMaxResults(1);

        try {
            return q.getSingleResult() != null;
        } catch (NoResultException ex) {
            return false;
        }
    }
}
