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

import org.iisg.delivery.record.entity.Record;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access Object of a Record.
 */
@Repository
public class RecordDAOImpl implements RecordDAO {
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
     * Add a Record to the database.
     * @param obj Record to add.
     */
    public void add(Record obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a Record from the database.
     * @param obj Record to remove.
     */
    public void remove(Record obj) {
        try {
            obj = entityManager.getReference(Record.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

    /**
     * Save changes to a Record in the database.
     * @param obj Record to save.
     */
    public void save(Record obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the Record matching the given Id.
     * @param id Id of the Record to retrieve.
     * @return The Record matching the Id.
     */
    public Record getById(int id) {
        return entityManager.find(Record.class, id);
    }

    /**
     * Get a criteria builder for querying Records.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all Records matching a built query.
     * @param query The query to match by.
     * @return A list of matching Records.
     */
    public List<Record> list(CriteriaQuery<Record> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single Record matching a built query.
     * @param query The query to match by.
     * @return The matching Record.
     */
    public Record get(CriteriaQuery<Record> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (Record)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
