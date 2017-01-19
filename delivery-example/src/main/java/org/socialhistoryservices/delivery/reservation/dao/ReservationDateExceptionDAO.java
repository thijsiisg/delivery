package org.socialhistoryservices.delivery.reservation.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.socialhistoryservices.delivery.reservation.entity.ReservationDateException;

import java.util.List;

/**
 * Created by Igor on 1/10/2017.
 */
public interface ReservationDateExceptionDAO {
    /**
     * Add a ReservationDateExceptions to the database.
     * @param obj Reservation to add.
     */
    public void add(ReservationDateException obj);

    /**
     * Remove a ReservationDateExceptions from the database.
     * @param obj Reservation to remove.
     */
    public void remove(ReservationDateException obj);

    /**
     * Save changes to a ReservationDateExceptions in the database.
     * @param obj Reservation to save.
     */
    public void save(ReservationDateException obj);

    /**
     *
     * @param q
     * @return
     */
    public List<ReservationDateException> list(CriteriaQuery<ReservationDateException> q);

    /**
     * Get a criteria builder for querying ReservationDateExceptions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     *
     * @param id
     * @return
     */
    public ReservationDateException getById(int id);
}
