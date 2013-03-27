
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

package org.socialhistoryservices.delivery.reservation.dao;

import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access bject of a reservation.
 */
public interface ReservationDAO {
    /**
     * Add a Reservation to the database.
     * @param obj Reservation to add.
     */
    public void add(Reservation obj);

    /**
     * Remove a Reservation from the database.
     * @param obj Reservation to remove.
     */
    public void remove(Reservation obj);

    /**
     * Save changes to a Reservation in the database.
     * @param obj Reservation to save.
     */
    public void save(Reservation obj);

    /**
     * Retrieve the Reservation matching the given Id.
     * @param id Id of the Reservation to retrieve.
     * @return The Reservation matching the Id.
     */
    public Reservation getById(int id);

    /**
     * Get a criteria builder for querying Reservations.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all Reservations matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching Reservations.
     */
    public List<Reservation> list(CriteriaQuery<Reservation> q);

    /**
     * Get a single Reservation matching a built query.
     * @param query The query to match by.
     * @return The matching Reservation.
     */
    public Reservation get(CriteriaQuery<Reservation> query);

    /**
     * Get an active reservation relating to a specific Holding.
     * @param h Holding to find a reservation for.
     * @return The active reservation, null if none exist.
     */
    public Reservation getActiveFor(Holding h);

    /**
     * Check whether there are any reservations made on the holding.
     * @param h Holding to check for reservations for.
     * @return Whether any reservations have been made including this holding.
     */
    public boolean hasReservations(Holding h);
}
