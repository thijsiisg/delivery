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

package org.socialhistoryservices.delivery.permission.service;

import org.socialhistoryservices.delivery.permission.entity.Permission;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.socialhistoryservices.delivery.record.entity.Record;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Interface to represent the service of the permission package.
 */
public interface PermissionService {
    /**
     * Add a Permission to the database.
     * @param obj Permission to add.
     */
    public void addPermission(Permission obj);

    /**
     * Remove a Permission from the database.
     * @param obj Permission to remove.
     */
    public void removePermission(Permission obj);

    /**
     * Remove a RecordPermission from the database.
     * @param obj RecordPermission to remove.
     */
    public void removeRecordPermission(RecordPermission obj);

    /**
     * Save changes to a Permission in the database.
     * @param obj Permission to save.
     */
    public void savePermission(Permission obj);

    /**
     * Retrieve the Permission matching the given Id.
     * @param id Id of the Permission to retrieve.
     * @return The Permission matching the Id.
     */
    public Permission getPermissionById(int id);

    /**
     * Get a criteria builder for querying Permissions.
     * @return the CriteriaBuilder.
     */
    public CriteriaBuilder getPermissionCriteriaBuilder();

    /**
     * List all RecordPermissions matching a built query.
     * @param query The query to match by.
     * @return A list of matching Permissions.
     */
    public List<RecordPermission> listPermissions(CriteriaQuery<RecordPermission> query);

    /**
     * Get a single Permission matching a built query.
     * @param query The query to match by.
     * @return The matching Permission.
     */
    public Permission getPermission(CriteriaQuery<Permission> query);

    /**
     * Fetch a permission by its code.
     * @param code The code of a permission.
     * @return The permission, or null if not found.
     */
    public Permission getPermissionByCode(String code);

    /**
     * Check whether there are any permission requests made on the record.
     * @param record Record to check for permission requests for.
     * @return Whether any permission requests have been made including this record.
     */
    public boolean hasPermissions(Record record);
}
