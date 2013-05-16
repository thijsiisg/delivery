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

package org.socialhistoryservices.delivery.permission.entity;

import org.socialhistoryservices.delivery.record.entity.Record;

import javax.persistence.*;

/**
 * A permission on a specific restricted record (links a permission request
 * to records, specifying for each record whether permission is granted or not).
 */
@Entity
@Table(name="recordpermissions")
public class RecordPermission {
    /** The RecordPermission's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the RecordPermission's id.
     * @return the RecordPermission's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the RecordPermission's id.
     * @param id the RecordPermission's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** The RecordPermission's granted. */
    @Column(name="granted", nullable=false)
    private boolean granted;

    /**
     * Get the RecordPermission's granted.
     * @return the RecordPermission's granted.
     */
    public boolean getGranted() {
        return granted;
    }

    /**
     * Set the RecordPermission's granted.
     * @param granted the RecordPermission's granted.
     */
    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    @Column(name="motivation", columnDefinition = "TEXT")
    private String motivation;

    /**
     * Set the RecordPermission's motivation.
     * @param mot The motivation string.
     */
    public void setMotivation(String mot) {
        this.motivation = mot;
    }

    /**
     * Get the motivation.
     * @return The motivation string.
     */
    public String getMotivation() {
        return motivation;
    }

    /** The RecordPermission's permission. */
    @ManyToOne
    @JoinColumn(name="permission_id")
    private Permission permission;

    /**
     * Get the RecordPermission's permission.
     * @return the RecordPermission's permission.
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Set the RecordPermission's permission.
     * @param permission the RecordPermission's permission.
     */
    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    /** The RecordPermission's record. */
    @ManyToOne
    @JoinColumn(name="record_id")
    private Record record;

    /**
     * Get the RecordPermission's record.
     * @return the RecordPermission's record.
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Set the RecordPermission's record.
     * @param record the RecordPermission's record.
     */
    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * Default contructor.
     */
    public RecordPermission() {
        setGranted(false);
    }

}