package org.socialhistoryservices.delivery.reproduction.dao;

import org.socialhistoryservices.delivery.reproduction.entity.ReproductionCustomNote;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access object of a reproduction standard option.
 */
public interface ReproductionCustomNoteDAO {
    /**
     * Add a ReproductionCustomNote to the database.
     *
     * @param obj ReproductionCustomNote to add.
     */
    public void add(ReproductionCustomNote obj);

    /**
     * Remove a ReproductionCustomNote from the database.
     *
     * @param obj ReproductionCustomNote to remove.
     */
    public void remove(ReproductionCustomNote obj);

    /**
     * Save changes to a ReproductionCustomNote in the database.
     *
     * @param obj ReproductionCustomNote to save.
     */
    public void save(ReproductionCustomNote obj);

    /**
     * Retrieve the ReproductionCustomNote matching the given Id.
     *
     * @param id Id of the ReproductionCustomNote to retrieve.
     * @return The ReproductionCustomNote matching the Id.
     */
    public ReproductionCustomNote getById(int id);

    /**
     * Get a criteria builder for querying ReproductionCustomNotes.
     *
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * List all ReproductionCustomNotes matching a built query.
     *
     * @param q The criteria query to execute
     * @return A list of matching ReproductionCustomNotes.
     */
    public List<ReproductionCustomNote> list(CriteriaQuery<ReproductionCustomNote> q);

    /**
     * Get a single ReproductionCustomNote matching a built query.
     *
     * @param query The query to match by.
     * @return The matching ReproductionCustomNote.
     */
    public ReproductionCustomNote get(CriteriaQuery<ReproductionCustomNote> query);

    /**
     * List all ReproductionCustomNotes by material type.
     *
     * @return A list of ReproductionCustomNotes.
     */
    public List<ReproductionCustomNote> listAll();
}
