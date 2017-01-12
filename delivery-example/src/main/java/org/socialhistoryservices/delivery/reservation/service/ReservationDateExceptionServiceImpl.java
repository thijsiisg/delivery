package org.socialhistoryservices.delivery.reservation.service;

import org.apache.log4j.Logger;
import org.socialhistoryservices.delivery.reservation.dao.ReservationDateExceptionDAO;
import org.socialhistoryservices.delivery.reservation.entity.ReservationDateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Igor on 1/10/2017.
 */
@Service
@Transactional
public class ReservationDateExceptionServiceImpl implements ReservationDateExceptionService {
    @Autowired
    private ReservationDateExceptionDAO reservationDateExceptionDAO;

    private Logger log = Logger.getLogger(getClass());
    /**
     * Add a ReservationDateException to the database.
     * @param obj ReservationDateException to add.
     */
    public void addReservationDateException(ReservationDateException obj){
        reservationDateExceptionDAO.add(obj);
    }

    /**
     * Remove a ReservationDateException from the database.
     * @param obj ReservationDateException to remove.
     */
    public void removeReservationDateException(ReservationDateException obj){
        reservationDateExceptionDAO.remove(obj);
    }

    /**
     * Save changes to a ReservationDateException in the database.
     * @param obj ReservationDateException to save.
     */
    public void saveReservationDateException(ReservationDateException obj){

    }

    /**
     * List all ReservationDateExceptions matching a built query.
     * @param q The criteria query to execute
     * @return A list of matching ReservationDateExceptions.
     */
    public List<ReservationDateException> listReservationDateExceptions(CriteriaQuery<ReservationDateException> q) {
        return reservationDateExceptionDAO.list(q);
    }

    /**
     * Get a criteria builder for querying ReservationDateExceptions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getReservationDateExceptionCriteriaBuilder() { return reservationDateExceptionDAO.getCriteriaBuilder(); }
}
