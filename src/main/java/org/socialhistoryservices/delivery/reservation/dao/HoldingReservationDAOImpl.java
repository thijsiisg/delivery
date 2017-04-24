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

package org.socialhistoryservices.delivery.reservation.dao;

import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Represents the Data Access object of a holding reservation.
 */
@Repository
public class HoldingReservationDAOImpl implements HoldingReservationDAO {
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
     * Get a criteria builder for querying HoldingReservations.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    /**
     * List all HoldingReservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching HoldingReservations.
     */
    public List<HoldingReservation> list(CriteriaQuery<HoldingReservation> q) {
        return entityManager.createQuery(q).getResultList();
    }

    /**
     * List all HoldingReservations matching a built query.
     * @param q The criteria query to execute
     * @param firstResult The first result to obtain
     * @param maxResults The max number of results to obtain
     * @return A list of matching HoldingReservations.
     */
    public List<HoldingReservation> list(CriteriaQuery<HoldingReservation> q, int firstResult, int maxResults) {
        return entityManager
            .createQuery(q)
            .setFirstResult(firstResult)
            .setMaxResults(maxResults)
            .getResultList();
    }

    /**
     * Count all HoldingReservations matching a built query.
     * @param q The criteria query to execute
     * @return The number of counted results.
     */
    public long count(CriteriaQuery<Long> q) {
        return entityManager.createQuery(q).getSingleResult();
    }
}