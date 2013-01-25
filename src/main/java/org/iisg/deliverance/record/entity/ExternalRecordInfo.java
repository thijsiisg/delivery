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

package org.iisg.deliverance.record.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents external info for a record (title from evergreen,
 * author etc.). This information is cached, cannot be relied upon to be
 * persistent.
 */
@Entity
@Table(name="external_record_info")
public class ExternalRecordInfo {

    public enum MaterialType {
        SERIAL,
        BOOK,
        SOUND,
        DOCUMENTATION,
        ARCHIVE,
        VISUAL,
        OTHER
    }

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


    
     /** The Record's title. */
    @NotBlank
    @Size(max=255)
    @Column(name="title", nullable=false)
    private String title;


    /**
     * Get the Record's title.
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the Record's title.
     * @param title The title.
     */
    public void setTitle(String title) {
        this.title = title;
    }


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name="material_type", nullable=false)
    private MaterialType materialType;

    /**
     * Get external material type.
     * @return The external material type, do not rely on this too much.
     */
    public MaterialType getMaterialType() {
        return materialType;
    }

    /**
     * Se the material type.
     * @param type The material type to set.
     */
    public void setMaterialType(MaterialType type) {
        materialType = type;
    }

    @Size(max=255)
    @Column(name="author", nullable=true)
    private String author;

    /**
     * Get the author.
     * @return The author if applicable (i.e. archives do not have an author,
     * but books do).
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author.
     * @param author The value for the author.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    @Size(max=30)
    @Column(name="year", nullable=true)
    private String displayYear;

    /**
     * Get the display year, contains all kinds of string characters ("ca."
     * "-" etc.),
     * therefore it is a string instead of an int.
     * @return The year to display.
     */
    public String getDisplayYear() {
        return displayYear;
    }

    /**
     * Set the display year.
     * @param year The year to set for display.
     */
    public void setDisplayYear(String year) {
         displayYear = year;
    }


}
