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

package org.socialhistoryservices.delivery.record.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.NotBlank;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Holding information associated with a Record.
 */
@Entity
@Table(name="holdings")
public class Holding {


    /** The usage restriction of the holding. */
    public enum UsageRestriction {
        OPEN,
        CLOSED
    }

     /** Status of the holding. */
    public enum Status {
        AVAILABLE,
        RESERVED,
        IN_USE,
        RETURNED,
    }



    /** The Holding's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the Holding's id.
     * @return the Holding's id.
     */
    public int getId() {
        return id;
    }

    /** The Holding's type. */
    @NotBlank
    @Column(name="signature", nullable=false)
    private String signature;

    /**
     * Get the Holding's type.
     * @return the Holding's type.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Set the Holding's type.
     * @param sig the Holding's type.
     */
    public void setSignature(String sig) {
        signature = sig;

	    if (signature.trim().endsWith(".x") || signature.trim().startsWith("No circulation")) {
			this.setUsageRestriction(UsageRestriction.CLOSED);
	    }
    }

    /** The Holding's floor (nullable). */
    @Min(0)
    @Column(name="floor")
    private Integer floor;

    /**
     * Get the Holding's floor.
     * @return the Holding's floor.
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * Set the Holding's floor.
     * @param floor the Holding's floor.
     */
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    /** The Holding's direction. */
    @Size(max=50)
    @Column(name="direction")
    private String direction;

    /**
     * Get the Holding's direction.
     * @return the Holding's direction.
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Set the Holding's direction.
     * @param direction the Holding's direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /** The Holding's cabinet (nullable). */
    @Size(max=50)
    @Column(name="cabinet")
    private String cabinet;

    /**
     * Get the Holding's cabinet.
     * @return the Holding's cabinet.
     */
    public String getCabinet() {
        return cabinet;
    }

    /**
     * Set the Holding's cabinet.
     * @param cabinet the Holding's cabinet.
     */
    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    /** The Holding's shelf (nullable). */
    @Size(max=50)
    @Column(name="shelf")
    private String shelf;

    /**
     * Get the Holding's shelf.
     * @return the Holding's shelf.
     */
    public String getShelf() {
        return shelf;
    }

    /**
     * Set the Holding's shelf.
     * @param shelf the Holding's shelf.
     */
    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    /** The Holding's record. */
    @NotNull
    @ManyToOne
    @JoinColumn(name="record_id")
    private Record record;

    /**
     * Get the Holding's record.
     * @return the Holding's record.
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Set the Holding's record.
     * @param record the Holding's record.
     */
    public void setRecord(Record record) {
        this.record = record;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="usage_restriction", nullable=false)
    private UsageRestriction usageRestriction;

    /**
     * Get the usage restriction.
     * @return The value of the user restriction.
     */
    public UsageRestriction getUsageRestriction() {
        return usageRestriction;
    }

    /**
     * Set the Holding's usage restriction.
     * @param u The value to set the usage to.
     */
    public void setUsageRestriction(UsageRestriction u) {
        usageRestriction = u;
    }

    /** The Holding's status. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private Status status;

    /**
     * Get the Holding's status.
     * @return the Record's status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set the Holding's status.
     * @param status the Record's status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }


    @OneToOne(cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name="external_info_id")
    private ExternalHoldingInfo externalInfo;

    /**
     * Get the external holding info.
     * @return The info object.
     */
    public ExternalHoldingInfo getExternalInfo() {
        return externalInfo;
    }

    /**
     * Set the external info (preferably from IISHRecordLookupService
     * .getRecordMetaDataByPid).
     * @param info The info.
     */
    public void setExternalInfo(ExternalHoldingInfo info) {
        this.externalInfo = info;
    }

	@OneToMany(mappedBy="holding", cascade=CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<HoldingReservation> holdingReservations;

	public void setHoldingReservations(List<HoldingReservation> hrs) {
		holdingReservations = hrs;
	}

	public List<HoldingReservation> getHoldingReservations() {
		return holdingReservations;
	}

    /**
     * Merge other's fields with this holding. All fields except ID,
     * signature and status are merged.
     * @param other The other holding.
     */
    public void mergeWith(Holding other) {
        setCabinet(other.getCabinet());
        setDirection(other.getDirection());
        setFloor(other.getFloor());
        setShelf(other.getShelf());
        setUsageRestriction(other.getUsageRestriction());
        setExternalInfo(other.getExternalInfo());
    }

    /**
     * Add default data.
     */
    public Holding() {
        setStatus(Status.AVAILABLE);
        setUsageRestriction(UsageRestriction.OPEN);
	    // TODO: Test if adding an empty ExternalHoldingInfo object fixes the holding.externalinfo fix.
	    setExternalInfo(new ExternalHoldingInfo());
    }

    public String toString() {
        return String.valueOf(id);
    }
}
