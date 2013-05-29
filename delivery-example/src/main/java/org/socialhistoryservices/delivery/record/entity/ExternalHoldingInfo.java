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

/**
 * Extra info extracted from the external API.
 */
@Entity
@Table(name="external_holding_info")
public class ExternalHoldingInfo {

    /** The id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the info's id.
     * @return the info's id.
     */
    public int getId() {
        return id;
    }



    @Column(name="serialNumbers", columnDefinition = "TEXT", nullable=true)
    private String serialNumbers;

    /**
     * Get the available serial numbers.
     * @return The serial numbers available.
     */
    public String getSerialNumbers() {
        return serialNumbers;
    }

    /**
     * Set the available serial numbers.
     * @param nrs The available serial numbers.
     */
    public void setSerialNumbers(String nrs) {
         serialNumbers = nrs;
    }
}
