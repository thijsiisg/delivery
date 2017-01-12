package org.socialhistoryservices.delivery.reservation.service;

import org.socialhistoryservices.delivery.reservation.entity.ReservationDateException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Igor on 1/10/2017.
 */
public interface ReservationDateExceptionService {

    /**
     * Add a ReservationDateException to the database.
     * @param obj ReservationDateException to add.
     */
    public void addReservationDateException(ReservationDateException obj);

    /**
     * Remove a ReservationDateException from the database.
     * @param obj ReservationDateException to remove.
     */
    public void removeReservationDateException(ReservationDateException obj);

    /**
     * Save changes to a ReservationDateException in the database.
     * @param obj ReservationDateException to save.
     */
    public void saveReservationDateException(ReservationDateException obj);

    /**
     * List all ReservationDateExceptions matching a built query.
     * @param q The criteria query to execute.
     * @return A list of matching ReservationDateExceptions.
     */
    public List<ReservationDateException> listReservationDateExceptions(CriteriaQuery<ReservationDateException> q);

    /**
     * Get a criteria builder for querying ReservationDateExceptions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReservationDateExceptionCriteriaBuilder();
}
