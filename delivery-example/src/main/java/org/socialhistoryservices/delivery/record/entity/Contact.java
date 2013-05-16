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

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Contact information associated with a Record.
 */
@Entity
@Table(name="contacts")
public class Contact {
    /** The Contact's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the Contact's id.
     * @return the Contact's id.
     */
    public int getId() {
        return id;
    }

    /** The Contact's firstname. */
    @Size(max=255)
    @Column(name="firstname")
    private String firstname;

    /**
     * Get the Contact's firstname.
     * @return the Contact's firstname.
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Set the Contact's firstname.
     * @param firstname the Contact's firstname.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /** The Contact's lastname. */
    @Size(max=255)
    @Column(name="lastname")
    private String lastname;

    /**
     * Get the Contact's lastname.
     * @return the Contact's lastname.
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Set the Contact's lastname.
     * @param lastname the Contact's lastname.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /** The Contact's preposition. */
    @Size(max=255)
    @Column(name="preposition")
    private String preposition;

    /**
     * Get the Contact's preposition.
     * @return the Contact's preposition.
     */
    public String getPreposition() {
        return preposition;
    }

    /**
     * Set the Contact's preposition.
     * @param preposition the Contact's preposition.
     */
    public void setPreposition(String preposition) {
        this.preposition = preposition;
    }

    /** The Contact's address. */
    @Size(max=255)
    @Column(name="address")
    private String address;

    /**
     * Get the Contact's address.
     * @return the Contact's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the Contact's address.
     * @param address the Contact's address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** The Contact's zipcode. */
    @Size(max=255)
    @Column(name="zipcode")
    private String zipcode;

    /**
     * Get the Contact's zipcode.
     * @return the Contact's zipcode.
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Set the Contact's zipcode.
     * @param zipcode the Contact's zipcode.
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /** The Contact's location. */
    @Size(max=255)
    @Column(name="location")
    private String location;

    /**
     * Get the Contact's location.
     * @return the Contact's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the Contact's location.
     * @param location the Contact's location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** The Contact's email. */
    @Size(max=255)
    @Column(name="email")
    private String email;

    /**
     * Get the Contact's email.
     * @return the Contact's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the Contact's email.
     * @param email the Contact's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** The Contact's phone. */
    @Size(max=255)
    @Column(name="phone")
    private String phone;

    /**
     * Get the Contact's phone.
     * @return the Contact's phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the Contact's phone.
     * @param phone the Contact's phone.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** The Contact's fax. */
    @Size(max=255)
    @Column(name="fax")
    private String fax;

    /**
     * Get the Contact's fax.
     * @return the Contact's fax.
     */
    public String getFax() {
        return fax;
    }

    /**
     * Set the Contact's fax.
     * @param fax the Contact's fax.
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /** The country the contact is located in. */
    @Size(max=255)
    @Column(name="country")
    private String country;

    /**
     * Get the Contact's country.
     * @return The country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set the Contact's country.
     * @param country The new country.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Merge this contact with another contact's field values.
     * @param other The contact to merge from.
     */
    public void mergeWith(Contact other) {
        setAddress(other.getAddress());
        setCountry(other.getCountry());
        setEmail(other.getEmail());
        setFax(other.getFax());
        setFirstname(other.getFirstname());
        setLastname(other.getLastname());
        setLocation(other.getLocation());
        setPhone(other.getPhone());
        setPreposition(other.getPreposition());
        setZipcode(other.getZipcode());
    }

    /**
     * Returns true iff all fields (except PK) are null.
     * @return Whether the above condition holds or not.
     */
    public boolean isEmpty() {
        return address == null &&
               country == null &&
               email == null &&
               fax == null &&
               firstname == null &&
               lastname == null &&
               location == null &&
               phone == null &&
               preposition == null &&
               zipcode == null;
    }


    /**
     * Set default contact data.
     */
    public Contact() {
       
    }
}
