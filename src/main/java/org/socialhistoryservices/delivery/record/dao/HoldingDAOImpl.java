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

package org.socialhistoryservices.record.dao;

import org.socialhistoryservices.record.entity.ExternalHoldingInfo;
import org.socialhistoryservices.record.entity.Holding;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access Object of the Holding data
 * associated with a record.
 */
@Repository
public class HoldingDAOImpl implements HoldingDAO {
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
     * Add a Holding to the database.
     * @param obj Holding to add.
     */
    public void add(Holding obj) {
        entityManager.persist(obj);
    }

    /**
     * Remove a Holding from the database.
     * @param obj Holding to remove.
     */
    public void remove(Holding obj) {
        try {
            obj = entityManager.getReference(Holding.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

	/**
	 * Remove the ExternalHoldingInfo of a Holding from the database.
	 * @param obj Holding of which to remove the ExternalHoldingInfo.
	 */
	public void removeExternalInfo(Holding obj) {
		try {
			ExternalHoldingInfo ehiObj = obj.getExternalInfo();
			ehiObj = entityManager.getReference(ExternalHoldingInfo.class, ehiObj.getId());
			entityManager.remove(ehiObj);
		} catch (EntityNotFoundException ignored) {}
	}

    /**
     * Save changes to a Holding in the database.
     * @param obj Holding to save.
     */
    public void save(Holding obj) {
        entityManager.merge(obj);
    }

    /**
     * Retrieve the Holding matching the given Id.
     * @param id Id of the Holding to retrieve.
     * @return The Holding matching the Id.
     */
    public Holding getById(int id) {
        return entityManager.find(Holding.class, id);
    }

    /**
     * Get a criteria builder for querying Holdings.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all Holdings matching a built query.
     * @param query The query to match by.
     * @return A list of matching Holdings.
     */
    public List<Holding> list(CriteriaQuery<Holding> query) {
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get a single Holding matching a built query.
     * @param query The query to match by.
     * @return The matching Holding.
     */
    public Holding get(CriteriaQuery<Holding> query) {
        try {
            TypedQuery q = entityManager.createQuery(query);
            q.setMaxResults(1);
            return (Holding)q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
