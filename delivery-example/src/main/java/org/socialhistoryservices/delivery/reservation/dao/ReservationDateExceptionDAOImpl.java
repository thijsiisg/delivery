package org.socialhistoryservices.delivery.reservation.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.socialhistoryservices.delivery.reservation.entity.ReservationDateException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * Created by Igor on 1/10/2017.
 */
@Repository
public class ReservationDateExceptionDAOImpl implements ReservationDateExceptionDAO {
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
     * Add a Reservation to the database.
     * @param obj Reservation to add.
     */
    public void add(ReservationDateException obj){
        entityManager.persist(obj);
    }

    /**
     * Remove a Reservation from the database.
     * @param obj Reservation to remove.
     */
    public void remove(ReservationDateException obj){
        try{
            obj = entityManager.getReference(ReservationDateException.class, obj.getId());
            entityManager.remove(obj);
        } catch (EntityNotFoundException ignored) {}
    }

    /**
     * Save changes to a Reservation in the database.
     * @param obj Reservation to save.
     */
    public void save(ReservationDateException obj){
        entityManager.merge(obj);
    }

    /**
     *
     * @param q
     * @return
     */
    public List<ReservationDateException> list(CriteriaQuery<ReservationDateException> q) {
        return entityManager.createQuery(q).getResultList();
    }

    /**
     * Get a criteria builder for querying ReservationDateExceptions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder() { return entityManager.getCriteriaBuilder(); }
}
