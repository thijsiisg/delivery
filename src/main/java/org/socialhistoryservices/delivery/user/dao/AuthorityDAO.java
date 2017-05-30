package org.socialhistoryservices.delivery.user.dao;

import org.socialhistoryservices.delivery.user.entity.Authority;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access Object of the user permissions.
 */
public interface AuthorityDAO {
    /**
     * Add a Authority to the database.
     * @param obj Authority to add.
     */
    public void add(Authority obj);

    /**
     * Remove a Authority from the database.
     * @param obj Authority to remove.
     */
    public void remove(Authority obj);

    /**
     * Save changes to a Authority in the database.
     * @param obj Authority to save.
     */
    public void save(Authority obj);

    /**
     * Retrieve the Authority matching the given Id.
     * @param id Id of the Authority to retrieve.
     * @return The Authority matching the Id.
     */
    public Authority getById(int id);

    /**
     * Get a criteria builder for querying Authorities.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all Authorities matching a built query.
     * @param query The query to match by.
     * @return A list of matching authorities.
     */
    public List<Authority> list(CriteriaQuery<Authority> query);

    /**
     * Get a single Authority matching a built query.
     * @param query The query to match by.
     * @return The matching Authority.
     */
    public Authority get(CriteriaQuery<Authority> query);
}
