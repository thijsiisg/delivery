package org.iisg.delivery.record.entity;

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
