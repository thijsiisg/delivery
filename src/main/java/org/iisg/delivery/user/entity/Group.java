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

package org.iisg.delivery.user.entity;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

/**
 * A Group a user can be in.
 */
@Entity
@Table(name="groups")
public class Group {
    /** The Group's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the Group's id.
     * @return the Group's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the Group's id.
     * @param id the Group's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** The Group's name. */
    @Column(name="name", nullable=false)
    private String name;

    /**
     * Get the Group's name.
     * @return the Group's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Group's name.
     * @param name the Group's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /** The Group's description. */
    @Column(name="description", nullable=false)
    private String description;

    /**
     * Get the Group's description.
     * @return the Group's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the Group's description.
     * @param description the Group's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** The Group's permissions. */
    @ManyToMany
    @JoinTable(name="group_permissions",
      joinColumns=@JoinColumn(name="group_id"),
      inverseJoinColumns=@JoinColumn(name="permission_id"))

    private Set<Authority> permissions;

    /**
     * Get the Group's permissions.
     * @return the Group's permissions.
     */
    public Set<Authority> getPermissions() {
        return permissions;
    }

    /**
     * Check whether this group has a specific permission.
     * @param permission The permission type to check for.
     * @return Whether this group has the permission.
     */
    public boolean hasPermission(String permission) {
        for (Authority perm : permissions) {
            if (perm.getName().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set defaults.
     */
    public Group() {
        permissions = new HashSet<Authority>();
        setName("");
        setDescription("");
    }
}
