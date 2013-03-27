
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

package org.socialhistoryservices.delivery.record.service;

import org.socialhistoryservices.delivery.api.NoSuchPidException;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.record.entity.Record;
import org.springframework.validation.BindingResult;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface representing the service of the Record package.
 */
public interface RecordService {
    /**
     * Add a Record to the database.
     * @param obj Record to add.
     */
    public void addRecord(Record obj);

    /**
     * Remove a Record from the database.
     * @param obj Record to remove.
     */
    public void removeRecord(Record obj);

    /**
     * Save changes to a Record in the database.
     * @param obj Record to save.
     */
    public void saveRecord(Record obj);

    /**
     * Retrieve the Record matching the given Id.
     * @param id Id of the Record to retrieve.
     * @return The Record matching the Id.
     */
    public Record getRecordById(int id);

    /**
     * Resolve the most specific matching the given Pid.
     * @param pid Fully qualified Pid to retrieve.
     * @return The Record most specifically matching the Pid. Null if none exist.
     */
    public Record resolveRecordByPid(String pid);

    /**
     * Retrieve the Record matching the given pid.
     * @param pid Pid of the Record to retrieve.
     * @return The Record matching the pid. Null if none exist.
     */
    public Record getRecordByPid(String pid);

    /**
     * Get a criteria builder for querying Records.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getRecordCriteriaBuilder();

    /**
     * List all Records matching a built query.
     * @param query The query to match by.
     * @return A list of matching Records.
     */
    public List<Record> listRecords(CriteriaQuery<Record> query);

    /**
     * Get a single Record matching a built query.
     * @param query The query to match by.
     * @return The matching Record.
     */
    public Record getRecord(CriteriaQuery<Record> query);

    /**
     * Retrieve the Holding matching the given Id.
     * @param id Id of the Holding to retrieve.
     * @return The Holding matching the Id.
     */
    public Holding getHoldingById(int id);

    /**
     * List all Holdings matching a built query.
     * @param query The query to match by.
     * @return A list of matching Holdings.
     */
    public List<Holding> listHoldings(CriteriaQuery<Holding> query);


    /**
     * Remove a Holding from the database.
     * @param obj Holding to remove.
     */
    public void removeHolding(Holding obj);

    /**
     * Scheduled task to update all closed records with embargo dates in the
     * past to open status.
     */
    public void checkEmbargoDates();

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
    public void createOrEdit(Record newRecord,
                                      Record oldRecord, BindingResult result)
            throws NoSuchPidException, NoSuchParentException;

    /**
     * Get the first available (not closed) holding for a record.
     * @param r The record to get a holding of.
     * @return The first free holding found or null if all occupied/no
     * holdings.
     */
    public Holding getAvailableHoldingForRecord(Record r);

    /**
     * Create a record, using the metadata from the IISH API to populate its
     * fields.
     * @param pid The pid of the record (should exist in the API).
     * @return The new Record (not yet committed to the database).
     * @throws NoSuchPidException Thrown when the provided PID does not exist
     * in the API.
     */
    public Record createRecordByPid(String pid) throws NoSuchPidException;
}
