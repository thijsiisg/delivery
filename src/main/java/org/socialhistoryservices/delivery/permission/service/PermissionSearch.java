package org.socialhistoryservices.delivery.permission.service;

import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.entity.Permission_;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission_;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo_;
import org.socialhistoryservices.delivery.record.entity.Record;
import org.socialhistoryservices.delivery.record.entity.Record_;
import org.socialhistoryservices.delivery.request.service.RequestSearch;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.Map;

/**
 * Permission search helper class, with support for paging.
 */
public class PermissionSearch extends RequestSearch<RecordPermission> {

    /**
     * Creates a new permission search helper.
     *
     * @param cb The criteria builder.
     * @param p  The parameters from the user.
     */
    public PermissionSearch(CriteriaBuilder cb, Map<String, String[]> p) {
        super(RecordPermission.class, cb, p);
    }

    /**
     * Build the query.
     *
     * @param rpRoot  The root entity.
     * @param cq      The query to build upon.
     * @param isCount Whether the query is a count or not.
     */
    @Override
    protected void build(Root<RecordPermission> rpRoot, CriteriaQuery<?> cq, boolean isCount) {
        Join<RecordPermission, Permission> pmRoot = rpRoot.join(RecordPermission_.permission);

        // Expression to be the where clause of the query
        Expression where = null;

        // Filters
        where = addDateFilter(p, cb, rpRoot, where);
        where = addNameFilter(p, cb, pmRoot, where);
        where = addEmailFilter(p, cb, pmRoot, where);
        where = addResearchOrganizationFilter(p, cb, pmRoot, where);
        where = addResearchSubjectFilter(p, cb, pmRoot, where);
        where = addAddressFilter(p, cb, pmRoot, where);
        where = addExplanationFilter(p, cb, pmRoot, where);
        where = addPermissionFilter(p, cb, rpRoot, where);
        where = addSearchFilter(p, cb, rpRoot, pmRoot, where);

        // Set the where clause
        if (where != null) {
            cq.where(where);
        }

        // Set sort order and sort column
        if (!isCount) {
            cq.orderBy(parseOrderFilter(p, cb, rpRoot, pmRoot));
        }
    }

    /**
     * Parse the sort and sort_dir filters into an Order to be used in a query.
     *
     * @param p      The parameter list to search the filter values in.
     * @param cb     The criteria builder used to construct the Order.
     * @param rpRoot The root of the record permission used to construct the Order.
     * @param pmRoot The root of the permission used to construct the Order.
     * @return The order the query should be in (asc/desc) sorted on provided
     * column. Defaults to asc on the PK column.
     */
    private Order parseOrderFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<RecordPermission> rpRoot,
                                   Join<RecordPermission, Permission> pmRoot) {
        boolean containsSort = p.containsKey("sort");
        boolean containsSortDir = p.containsKey("sort_dir");
        Expression e = pmRoot.get(Permission_.id);
        if (containsSort) {
            String sort = p.get("sort")[0];
            if (sort.equals("visitor_name")) {
                e = pmRoot.get(Permission_.name);
            }
            else if (sort.equals("date_granted")) {
                e = rpRoot.get(RecordPermission_.dateGranted);
            }
            else if (sort.equals("permission")) {
                e = rpRoot.get(RecordPermission_.granted);
            }
        }
        if (containsSortDir && p.get("sort_dir")[0].toLowerCase().equals("asc")) {
            return cb.asc(e);
        }
        return cb.desc(e);
    }

    /**
     * Add the search filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param rpRoot The record permission root.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addSearchFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<RecordPermission> rpRoot,
                                       Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("search") && !p.get("search")[0].trim().equals("")) {
            String search = p.get("search")[0].trim().toLowerCase();
            Join<RecordPermission, Record> record = rpRoot.join(RecordPermission_.record);
            Join<Record, ExternalRecordInfo> eRoot = record.join(Record_.externalInfo);
            Expression exSearch = cb.or(
                cb.like(cb.lower(eRoot.get(ExternalRecordInfo_.title)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.name)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.email)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.explanation)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.researchOrganization)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.researchSubject)),
                    "%" + search + "%"),
                cb.like(cb.lower(pmRoot.<String>get(Permission_.address)),
                    "%" + search + "%")
            );
            where = where != null ? cb.and(where, exSearch) : exSearch;
        }
        return where;
    }

    /**
     * Add the permission filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param rpRoot The record permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addPermissionFilter(Map<String, String[]> p, CriteriaBuilder cb, Root<RecordPermission> rpRoot,
                                           Expression where) {
        if (p.containsKey("permission")) {
            Expression exPermission = null;
            String permission = p.get("permission")[0].trim().toUpperCase();

            if (permission.equals("TRUE"))
                exPermission = cb.equal(rpRoot.<Boolean>get(RecordPermission_.granted), true);

            if (permission.equals("FALSE"))
                exPermission = cb.equal(rpRoot.<Boolean>get(RecordPermission_.granted), false);

            if (permission.equals("NULL"))
                exPermission = cb.isNull(rpRoot.<Date>get(RecordPermission_.dateGranted));

            if (exPermission != null)
                where = where != null ? cb.and(where, exPermission) : exPermission;
        }
        return where;
    }

    /**
     * Add the explanation filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addExplanationFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                            Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("explanation")) {
            Expression exExplanation = cb.like(
                pmRoot.<String>get(Permission_.explanation),
                "%" + p.get("explanation")[0].trim() + "%");
            where = where != null ? cb.and(where, exExplanation) : exExplanation;
        }
        return where;
    }

    /**
     * Add the address filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addAddressFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                        Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("address")) {
            Expression exAddress = cb.like(
                pmRoot.<String>get(Permission_.address),
                "%" + p.get("address")[0].trim() + "%");
            where = where != null ? cb.and(where, exAddress) : exAddress;
        }
        return where;
    }

    /**
     * Add the research subject filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addResearchSubjectFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("research_subject")) {
            Expression exResearch = cb.like(
                pmRoot.<String>get(Permission_.researchSubject),
                "%" + p.get("research_subject")[0].trim() + "%");
            where = where != null ? cb.and(where, exResearch) : exResearch;
        }
        return where;
    }

    /**
     * Add the research organization filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addResearchOrganizationFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                                     Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("research_organization")) {
            Expression exResearchOrg = cb.like(
                pmRoot.<String>get(Permission_.researchOrganization),
                "%" + p.get("research_organization")[0].trim() + "%");
            where = where != null ? cb.and(where, exResearchOrg) : exResearchOrg;
        }
        return where;
    }

    /**
     * Add the email filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addEmailFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                      Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("visitor_email")) {
            Expression exEmail = cb.like(
                pmRoot.<String>get(Permission_.email),
                "%" + p.get("visitor_email")[0].trim() + "%");
            where = where != null ? cb.and(where, exEmail) : exEmail;
        }
        return where;
    }

    /**
     * Add the name filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addNameFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                     Join<RecordPermission, Permission> pmRoot, Expression where) {
        if (p.containsKey("visitor_name")) {
            Expression exName = cb.like(pmRoot.<String>get(Permission_.name),
                "%" + p.get("visitor_name")[0].trim() + "%");
            where = where != null ? cb.and(where, exName) : exName;
        }
        return where;
    }

    /**
     * Add the date granted filter to the where clause, if present.
     *
     * @param p      The parameter list to search the given filter value in.
     * @param cb     The criteria builder.
     * @param pmRoot The permission root.
     * @param where  The already present where clause or null if none present.
     * @return The (updated) where clause, or null if the filter did not exist.
     */
    private Expression addDateFilter(Map<String, String[]> p, CriteriaBuilder cb,
                                     Root<RecordPermission> rpRoot, Expression where) {
        Date date = getDateFilter(p);
        if (date != null) {
            Expression<Boolean> exDate = cb.equal(rpRoot.<Date>get(RecordPermission_.dateGranted), date);
            where = where != null ? cb.and(where, exDate) : exDate;
        }
        else {
            Date fromDate = getFromDateFilter(p);
            Date toDate = getToDateFilter(p);
            if (fromDate != null) {
                Expression<Boolean> exDate = cb.greaterThanOrEqualTo
                    (rpRoot.<Date>get(RecordPermission_.dateGranted), fromDate);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
            if (toDate != null) {
                Expression<Boolean> exDate = cb.lessThanOrEqualTo(
                    rpRoot.<Date>get(RecordPermission_.dateGranted), toDate);
                where = where != null ? cb.and(where, exDate) : exDate;
            }
        }
        return where;
    }
}
