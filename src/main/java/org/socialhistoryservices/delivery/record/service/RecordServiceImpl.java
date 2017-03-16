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

package org.socialhistoryservices.delivery.record.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.socialhistoryservices.delivery.api.NoSuchPidException;
import org.socialhistoryservices.delivery.api.RecordLookupService;
import org.socialhistoryservices.delivery.config.DeliveryProperties;
import org.socialhistoryservices.delivery.record.dao.HoldingDAO;
import org.socialhistoryservices.delivery.record.dao.RecordDAO;
import org.socialhistoryservices.delivery.record.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Represents the service of the record package.
 */
@Service
@Transactional
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDAO recordDAO;

    @Autowired
    private HoldingDAO holdingDAO;

    @Autowired
    private DeliveryProperties deliveryProperties;

    @Autowired
    private Validator mvcValidator;

    @Autowired
    private RecordLookupService lookup;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private static final Log LOGGER = LogFactory.getLog(RecordServiceImpl.class);

    /**
     * Add a Record to the database.
     * @param obj Record to add.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRecord(Record obj) {
        recordDAO.add(obj);
    }

    /**
     * Remove a Record from the database.
     * @param obj Record to remove.
     */
    public void removeRecord(Record obj) {
        recordDAO.remove(obj);
    }

    /**
     * Save changes to a Record in the database.
     * @param obj Record to save.
     */
    public void saveRecord(Record obj) {
        recordDAO.save(obj);
    }

    /**
     * Save changes to a Holding in the database.
     * @param obj Holding to save.
     */
    public void saveHolding(Holding obj) {
        holdingDAO.save(obj);
    }

    /**
     * Retrieve the Record matching the given Id.
     * @param id Id of the Record to retrieve.
     * @return The Record matching the Id.
     */
    public Record getRecordById(int id) {
        return recordDAO.getById(id);
    }

    /**
     * Retrieve the Record matching the given pid.
     * @param pid Pid of the Record to retrieve.
     * @return The Record matching the pid. Null if none exist.
     */
    public Record getRecordByPid(String pid) {
        CriteriaBuilder builder = getRecordCriteriaBuilder();

        CriteriaQuery<Record> query = builder.createQuery(Record.class);
        Root<Record> recRoot = query.from(Record.class);
        query.select(recRoot);

        query.where(builder.equal(recRoot.get(Record_.pid), pid));

        return getRecord(query);
    }

    /**
     * Resolve the most specific matching the given Pid.
     * @param pid Fully qualified Pid to retrieve.
     * @return The Record most specifically matching the Pid.
     */
    public Record resolveRecordByPid(String pid) {
        Record root = getRecordByPid(pid);
        if (root != null) {
            updateExternalInfo(root, false);
            saveRecord(root);
            return root;
        }
        String itemSeparator = deliveryProperties.getItemSeperator();
        while (pid.contains(itemSeparator)) {
            pid = pid.substring(0, pid.lastIndexOf(itemSeparator));
            Record rec = getRecordByPid(pid);
            if (rec != null) {
                updateExternalInfo(rec, false);
                saveRecord(rec);
                return rec;
            }
        }
        return null;
    }

    /**
     * Get a criteria builder for querying Records.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getRecordCriteriaBuilder() {
        return recordDAO.getCriteriaBuilder();
    }

    /**
     * List all Records matching a built query.
     * @param query The query to match by.
     * @return A list of matching Records.
     */
    public List<Record> listRecords(CriteriaQuery<Record> query) {
        return recordDAO.list(query);
    }

    /**
     * List all Records.
     * @param offset     The offset.
     * @param maxResults The max number of records to fetch.
     * @return A list of Records.
     */
    public List<Record> listIterable(int offset, int maxResults) {
        return recordDAO.listIterable(offset, maxResults);
    }

    /**
     * Get a single Record matching a built query.
     * @param query The query to match by.
     * @return The matching Record.
     */
    public Record getRecord(CriteriaQuery<Record> query) {
        return recordDAO.get(query);
    }

    /**
     * List all Holdings matching a built query.
     * @param query The query to match by.
     * @return A list of matching Holdings.
     */
    public List<Holding> listHoldings(CriteriaQuery<Holding> query) {
        return holdingDAO.list(query);
    }

    /**
     * Retrieve the Holding matching the given Id.
     * @param id Id of the Holding to retrieve.
     * @return The Holding matching the Id.
     */
    public Holding getHoldingById(int id) {
        return holdingDAO.getById(id);
    }

    /**
     * Remove a Holding from the database.
     * @param obj Holding to remove.
     */
    public void removeHolding(Holding obj) {
        holdingDAO.remove(obj);
    }

    /**
     * Scheduled task to update all closed records with embargo dates in the
     * past to open status.
     */
    @Scheduled(fixedDelay=1000 * 60 * 60)
    public void checkEmbargoDates() {
        // Build the query
        CriteriaBuilder builder = getRecordCriteriaBuilder();

        CriteriaQuery<Record> query = builder.createQuery(Record.class);
        Root<Record> recRoot = query.from(Record.class);
        query.select(recRoot);

        Expression<Boolean> crit = builder.notEqual(recRoot.get(Record_.restrictionType),
                    Record.RestrictionType.OPEN);

        // Get earliest date
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.YEAR, -1000);
        Date earliest = cal.getTime();
        Date now = new Date();

        crit = builder.and(crit, builder.isNotNull(recRoot.get(Record_.embargo)));
        crit = builder.and(crit, builder.between(recRoot.get(Record_.embargo), earliest, now));
        query.where(crit);

        // Get list
        for (Record rec : listRecords(query)) {
            rec.setRestrictionType(Record.RestrictionType.OPEN);
            saveRecord(rec);
        }
    }

    /**
     * Updates the external info of the given record, if necessary.
     * @param record      The record of which to update the external info.
     * @param hardRefresh Always update the external info.
     */
    public void updateExternalInfo(Record record, boolean hardRefresh) {
        try {
            // Do we need to update the external info?
            Integer days = deliveryProperties.getExternalInfoMinDaysCache();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -days);

            Date lastUpdated = record.getExternalInfoUpdated();
            if (!hardRefresh && (lastUpdated != null) && lastUpdated.after(calendar.getTime()))
                return;

            // We need to update the external info
            String pid = record.getPid();
            ExternalRecordInfo eri = lookup.getRecordMetaDataByPid(pid);
            Map<String, ExternalHoldingInfo> ehMap = lookup.getHoldingMetadataByPid(pid);

            if (record.getExternalInfo() != null)
                record.getExternalInfo().mergeWith(eri);
            else
                record.setExternalInfo(eri);

            for (String signature : ehMap.keySet()) {
                boolean found = false;

                for (Holding h : record.getHoldings()) {
                    if (signature.equals(h.getSignature())) {
                        if (h.getExternalInfo() != null)
                            h.getExternalInfo().mergeWith(ehMap.get(signature));
                        else
                            h.setExternalInfo(ehMap.get(signature));

                        found = true;
                    }
                }

                if (!found) {
                    Holding holding = new Holding();
                    holding.setSignature(signature);
                    record.addHolding(holding);
                    holding.setRecord(record);
                }
            }

            record.setExternalInfoUpdated(new Date());
        }
        catch (NoSuchPidException nspe) {
            // PID not found, or API down, then just skip the record
        }
    }

    /**
     * Edit records.
     * @param newRecord The new record to put.
     * @param oldRecord The old record (or null if none).
     * @param result The binding result object to put the validation errors in.
     * @throws org.socialhistoryservices.delivery.api.NoSuchPidException Thrown when the
     * PID is not found in the external SRW API.
     * @throws org.socialhistoryservices.delivery.record.service.NoSuchParentException
     * Thrown when the provided record is detected as a pid by containing an
     * item separator (default .), but the parent record was not found in the
     * database.
     */
    public void createOrEdit(Record newRecord, Record oldRecord, BindingResult result)
            throws NoSuchPidException, NoSuchParentException {
        String pid = newRecord.getPid();

        String itemSeparator = deliveryProperties.getItemSeperator();
        if (pid.contains(itemSeparator)) {
            String parentPid = pid.substring(0, pid.lastIndexOf(itemSeparator));
            Record parent = getRecordByPid(parentPid);
            if (parent == null) {
                throw new NoSuchParentException();
            }
            newRecord.setParent(parent);
        } else {
            // Make sure the restriction type cannot be inherit on parents.
            if (newRecord.getRestrictionType() == Record.RestrictionType.INHERIT) {
                newRecord.setRestrictionType(Record.RestrictionType.OPEN);
            }
        }

        // Add holding/other API info if present
	    updateExternalInfo(newRecord, true);

        // Validate the record.
        validateRecord(newRecord, result);

        // Add or save the record when no errors are present.
        if (!result.hasErrors()) {
            if (oldRecord == null) {
                addRecord(newRecord);
            } else {
                oldRecord.mergeWith(newRecord);
                saveRecord(oldRecord);
            }
        }
    }

    /**
     * Validate a record using the provided binding result to store errors.
     * @param record The record.
     * @param result The binding result.
     */
    private void validateRecord(Record record, BindingResult result) {
        // Validate the record
        mvcValidator.validate(record, result);

        // Validate the associated contact if present
        if (record.getContact() != null) {
            result.pushNestedPath("contact");
            mvcValidator.validate(record.getContact(), result);
            result.popNestedPath();
        }

        // Validate associated holdings if present
        int i = 0;
        for(Holding h : record.getHoldings()) {
            // Set the record reference for newly created holdings (so you can
            // use the record reference without saving and loading them to
            // the database first).
            h.setRecord(record);
            result.pushNestedPath("holdings["+i+"]");
            mvcValidator.validate(h, result);
            result.popNestedPath();
            i++;
        }
    }

    /**
     * Get the first available (not closed) holding for a record.
     * @param r The record to get a holding of.
     * @param mustBeAvailable Whether the holding must be available.
     * @return The first free holding found or null if all occupied/no holdings.
     */
    public Holding getHoldingForRecord(Record r, boolean mustBeAvailable) {
        CriteriaBuilder cb = holdingDAO.getCriteriaBuilder();
        CriteriaQuery<Holding> cq = cb.createQuery(Holding.class);
        Root<Holding> hRoot = cq.from(Holding.class);
        cq.select(hRoot);

        Join<Holding, Record> rRoot = hRoot.join(
                    Holding_.record);
        Expression<Boolean> where = cb.equal(rRoot.get(Record_.id),
                r.getId());

        // Only get available holdings?
	    if (mustBeAvailable) {
		    where = cb.and(where, cb.equal(hRoot.<Holding.Status>get(Holding_.status), Holding.Status.AVAILABLE));
	    }

        // Only get holdings which may be used without an employee's explicit permission.
        where = cb.and(where, cb.equal(hRoot.<Holding.UsageRestriction>get(Holding_.usageRestriction),
		        Holding.UsageRestriction.OPEN));

        cq.where(where);

        return holdingDAO.get(cq);
    }

    /**
     * Create a record, using the metadata from the IISH API to populate its
     * fields.
     * @param pid The pid of the record (should exist in the API).
     * @return The new Record (not yet committed to the database).
     * @throws NoSuchPidException Thrown when the provided PID does not exist
     * in the API.
     */
    public Record createRecordByPid(String pid) throws NoSuchPidException {
        // 1). Assumed is provided pid is not yet in the system's local
        // database.

        Record r = new Record();
        r.setPid(pid);
        r.setExternalInfo(lookup.getRecordMetaDataByPid(pid));
        r.setParent(this.resolveRecordByPid(pid)); // Works because of 1).
        if (r.getParent() != null) {
            r.setRestrictionType(Record.RestrictionType.INHERIT);
        }
        List<Holding> hList = new ArrayList<Holding>();
        for (Map.Entry<String, ExternalHoldingInfo> e :
            lookup.getHoldingMetadataByPid(pid).entrySet()) {
            Holding h = new Holding();
            h.setSignature(e.getKey());
            h.setExternalInfo(e.getValue());
            h.setRecord(r);
            hList.add(h);
        }
        r.setHoldings(hList);
        return r;
    }
}
