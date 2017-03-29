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

import org.apache.commons.lang.RandomStringUtils;
import org.socialhistoryservices.delivery.record.entity.Record;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.util.*;

/**
 * A permission request in order to view a particular set of restricted records.
 */
@Entity
@Table(name="permissions")
public class Permission {
    /** The Permission's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the Permission's id.
     * @return the Permission's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the Permission's id.
     * @param id the Permission's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** The Permission's name. */
    @Column(name="name", nullable=false)
    private String name;

    /**
     * Get the Permission's name.
     * @return the Permission's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Permission's name.
     * @param name the Permission's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** The Permission's code. */
    @Column(name="code")
    private String code;

    /**
     * Get the Permission's code.
     * @return the Permission's code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the Permission's code.
     * @param code the Permission's code.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Generate a completely random code token.
     */
    public void generateCode() {
        code = RandomStringUtils.randomAlphanumeric(20);
    }

    /** The Permission's email. */
    @Column(name="email", nullable=false)
    private String email;

    /**
     * Get the Permission's email.
     * @return the Permission's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the Permission's email.
     * @param email the Permission's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** The Permission's researchSubject. */
    @Column(name="research_subject", nullable=false)
    private String researchSubject;

    /**
     * Get the Permission's researchSubject.
     * @return the Permission's researchSubject.
     */
    public String getResearchSubject() {
        return researchSubject;
    }

    /**
     * Set the Permission's researchSubject.
     * @param researchSubject the Permission's researchSubject.
     */
    public void setResearchSubject(String researchSubject) {
        this.researchSubject = researchSubject;
    }

    /** The Permission's researchOrganization. */
    @Column(name="research_organization", nullable=false)
    private String researchOrganization;

    /**
     * Get the Permission's researchOrganization.
     * @return the Permission's researchOrganization.
     */
    public String getResearchOrganization() {
        return researchOrganization;
    }

    /**
     * Set the Permission's researchOrganization.
     * @param researchOrganization the Permission's researchOrganization.
     */
    public void setResearchOrganization(String researchOrganization) {
        this.researchOrganization = researchOrganization;
    }

    /** The Permission's explanation. */
    @Column(name="explanation", columnDefinition="TEXT", nullable=false)
    private String explanation;

    /**
     * Get the Permission's explanation.
     * @return the Permission's explanation.
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Set the Permission's explanation.
     * @param explanation the Permission's explanation.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /** The Permission's address. */
    @Column(name="address", nullable=false)
    private String address;

    /**
     * Get the Permission's address.
     * @return the Permission's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the Permission's address.
     * @param address the Permission's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** The Permission's request locale. */
//    @Enumerated(EnumType.STRING)
    @Column(name="requestlocale", nullable=false)
    private Locale requestLocale;

    /**
     * Get the locale in which this permission was requested.
     * @return the Permission's request locale.
     */
    public Locale getRequestLocale() {
        return requestLocale;
    }

    /**
     * Set the Permission's request locale.
     * @param locale the Permission's request locale.
     */
    public void setRequestLocale(Locale locale) {
        requestLocale = locale;
    }

    /**
     * Permissions per record in this permission request.
     */
    @OneToMany(mappedBy="permission", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<RecordPermission> recordPermissions;

    /**
     * Get the per record permissions.
     * @return A list of permissions per record.
     */
    public List<RecordPermission> getRecordPermissions() {
        return recordPermissions;
    }

    /**
     * Add a record permission to a permission.
     * @param rp The record permission to add.
     */
    public void addRecordPermission(RecordPermission rp) {
        recordPermissions.add(rp);
    }

    /**
     * Remove a record permission from a permission.
     * @param rp The record permission to remove.
     */
    public void removeRecordPermission(RecordPermission rp) {
        recordPermissions.remove(rp);
    }

    /**
     * Check if this permission has a granted clause for
     * the given record.
     * @param rec Record to check for.
     * @return Whether permission has been granted for this record.
     */
    public boolean hasGranted(Record rec) {
        Calendar minDateCalender = Calendar.getInstance();
        minDateCalender.add(Calendar.YEAR, -1);

        for (RecordPermission p : recordPermissions) {
            boolean recordMatch = p.getRecord().equals(rec) ||
                ((rec.getParent() != null) && p.getRecord().equals(rec.getParent()));
            boolean valid = (p.getDateGranted() != null) && p.getDateGranted().after(minDateCalender.getTime());

            if (recordMatch && p.getGranted() && valid)
                return true;
        }
        return false;
    }

    /**
     * Default contstructor.
     */
    public Permission() {
        recordPermissions = new ArrayList<RecordPermission>();
        setRequestLocale(LocaleContextHolder.getLocale());
    }

}
