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

package org.socialhistoryservices.permission.controller;

import org.codehaus.jackson.JsonNode;
import org.hibernate.validator.constraints.Email;
import org.socialhistoryservices.permission.entity.Permission;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Form to handle modifying permissions.
 */
@ValidPermissionDateRange(from="dateFrom", to="dateTo")
public class PermissionForm {

    /** The name of the visitor to view the record. */
    @NotNull
    @Size(min=1, max=255)
    private String visitorName;
    /**
     * Get the Permission's visitorName.
     * @return The visitorName.
     */
    public String getVisitorName() {
        return visitorName;
    }
    /**
     * Set the Permission's visitorName.
     * @param visitorName The new visitorName.
     */
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    /** The email address of the visitor to view the record. */
    @NotNull
    @Email
    @Size(min=1,max=255)
    private String visitorEmail;
    /**
     * Get the Permission's visitorEmail.
     * @return The visitorEmail.
     */
    public String getVisitorEmail() {
        return visitorEmail;
    }
    /**
     * Set the Permission's visitorEmail.
     * @param visitorEmail The new visitorEmail.
     */
    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    /** The date from which the permission becomes active. */
    @NotNull
    @ValidPermissionDate
    private String dateFrom;
    /**
     * Get the Permission's date.
     * @return The date.
     */
    public String getDateFrom() {
        return dateFrom;
    }
    /**
     * Set the Permission's date.
     * @param date The new date.
     */
    public void setDateFrom(String date) {
        this.dateFrom = date;
    }

    /** The date until which the permission is active. */
    @NotNull
    @ValidPermissionDate
    private String dateTo;
    /**
     * Get the Permission's date.
     * @return The date.
     */
    public String getDateTo() {
        return dateTo;
    }
    /**
     * Set the Permission's date.
     * @param date The new date.
     */
    public void setDateTo(String date) {
        this.dateTo = date;
    }

    /**
     * The permission status. Can be null, in order to use the default
     * status.
     */
    private String status;
    /**
     * Get the Permission's status.
     * @return The status.
     */
    public String getStatus() {
        return status;
    }
    /**
     * Set the Permission's status.
     * @param status The new status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /** The address of the permission's applicant. */
    @NotNull
    @Size(min=1, max=255)
    private String address;
    /**
     * Get the Permission's address.
     * @return The address.
     */
    public String getAddress() {
        return address;
    }
    /**
     * Set the Permission's address.
     * @param address The new address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** The explanation of the permission's applicant. */
    @NotNull
    @Size(min=1)
    private String explanation;
    /**
     * Get the Permission's explanation.
     * @return The explanation.
     */
    public String getExplanation() {
        return explanation;
    }
    /**
     * Set the Permission's explanation.
     * @param explanation The new explanation.
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /** The researchOrganization of the permission's applicant. */
    @NotNull
    @Size(min=1, max=255)
    private String researchOrganization;
    /**
     * Get the Permission's research organization.
     * @return The the name of the organization.
     */
    public String getResearchOrganization() {
        return researchOrganization;
    }
    /**
     * Set the Permission's organization.
     * @param researchOrganization The new organization.
     */
    public void setResearchOrganization(String researchOrganization) {
        this.researchOrganization = researchOrganization;
    }

    /** The researchOrganization of the permission's applicant. */
    @NotNull
    @Size(min=1)
    private String researchSubject;
    /**
     * Get the Permission's research organization.
     * @return The the name of the organization.
     */
    public String getResearchSubject() {
        return researchSubject;
    }
    /**
     * Set the Permission's subject.
     * @param researchSubject The new subject.
     */
    public void setResearchSubject(String researchSubject) {
        this.researchSubject = researchSubject;
    }

    /**
     * Retrieve data from a model.
     * @param obj Permission to get data from.
     * @param df Date formatter
     */
    public void fillFrom(Permission obj, SimpleDateFormat df) {
        // Set up the form to edit
        setVisitorName(obj.getName());
        setVisitorEmail(obj.getEmail());
        setStatus(obj.getStatus().toString());
        setDateFrom(df.format(obj.getDateFrom()));
        setDateTo(df.format(obj.getDateTo()));
        setAddress(obj.getAddress());
        setExplanation(obj.getExplanation());
        setResearchOrganization(obj.getResearchOrganization());
        setResearchSubject(obj.getResearchSubject());
    }

    /**
     * Retrieve data from a json tree.
     * @param root The root of the json tree
     * @param df The date formatter
     */
    public void fillFrom(JsonNode root, SimpleDateFormat df) {
        // Metadata
        setVisitorName(root.path("visitor_name").getTextValue());
        setVisitorEmail(root.path("visitor_email").getTextValue());
        setAddress(root.path("address").getTextValue());
        setStatus(root.path("status").getTextValue());
        setExplanation(root.path("explanation").getTextValue());
        setResearchOrganization(root.path("research_organization").getTextValue());
        setResearchSubject(root.path("research_subject").getTextValue());
        setDateFrom(getDateValueFromNode(root.path("from_date"), df));
        setDateTo(getDateValueFromNode(root.path("to_date"), df));
    }

    /**
     * Get a date formatted according to df from a node.
     * @param node The node to get the date from.
     * @param df The date format to use.
     * @return Date formatted according to df or null if node was missing.
     */
    private String getDateValueFromNode(JsonNode node, SimpleDateFormat df) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (!node.isMissingNode()) {
            // Json-specified dates are always in this format, so
            // convert it to configured format first
            try {
                Date dt = format.parse(node.getTextValue());
                return df.format(dt);
            }
            catch (ParseException ex) {
                // Date is not used
            }
        }
        return null;
    }


    /**
     * Save data into a model.
     * @param obj Permission to save into.
     * @param df The date formatter
     */
    public void fillInto(Permission obj, SimpleDateFormat df) {
        obj.setName(getVisitorName());
        obj.setEmail(getVisitorEmail());

        if (getStatus() != null)
            obj.setStatus(Permission.Status.valueOf(getStatus()));
        obj.setAddress(getAddress());
        obj.setExplanation(getExplanation());
        obj.setResearchOrganization(getResearchOrganization());
        obj.setResearchSubject(getResearchSubject());
        try {
            obj.setDateFrom(df.parse(getDateFrom()));
        }
        catch (ParseException ex) {
            // Date is not used
        }
        try {
            obj.setDateTo(df.parse(getDateTo()));
        }
        catch (ParseException ex) {
            // Date is not used
        }
    }
}
