package org.socialhistoryservices.delivery.reproduction.dao;

import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the Data Access object of a holding reproduction.
 */
public interface HoldingReproductionDAO {
	/**
	 * Get a criteria builder for querying HoldingReproductions.
	 *
	 * @return the CriteriaBuilder.
	 */
	public CriteriaBuilder getCriteriaBuilder();

	/**
	 * List all HoldingReproductions matching a built query.
	 *
	 * @param q The criteria query to execute
	 * @return A list of matching HoldingReproductions.
	 */
	public List<HoldingReproduction> list(CriteriaQuery<HoldingReproduction> q);
}
