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

package org.iisg.deliverance.user.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.SecureRandom;
import java.math.BigInteger;
import javax.persistence.*;
import java.util.*;

/**
 * A user of the system.
 */
@Entity
@Table(name="users")
public class User implements UserDetails {
    /** The User's id. */
    @Id
    @GeneratedValue
    @Column(name="id")
    private int id;

    /**
     * Get the User's id.
     * @return the User's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the User's id.
     * @param id the User's id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** The User's username. */
    @Column(name="username", nullable=false)
    private String username;

    /**
     * Get the User's username.
     * @return the User's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the User's username.
     * @param username the User's username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** The group this user is in. */
    @ManyToMany
    @JoinTable(name="user_groups",
      joinColumns=@JoinColumn(name="user_id"),
      inverseJoinColumns=@JoinColumn(name="group_id"))
    private Set<Group> groups;

    /**
     * Get the User's groups.
     * @return The groups.
     */
    public Set<Group> getGroups() {
        return groups;
    }

    /**
     * Get the authorities this user has.
     * @return A collection of authorities (can be empty).
     */
    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (Group gr : getGroups()) {
            roles.addAll(gr.getPermissions());
        }
        return roles;
    }

    /**
     * Returns 'noPassWithCas' since CAS implementation does not use this.
     * @return The password.
     */
    public String getPassword() {
       return "noPassWithCas";
    }

    public boolean isAccountNonExpired() {
       return true;
    }

    public boolean isAccountNonLocked() {
       return true;
    }

    public boolean isCredentialsNonExpired() {
       return true;
    }

    public boolean isEnabled() {
       return true;
    }

    /**
     * Set defaults.
     */
    public User() {
        setUsername("");
        groups = new HashSet<Group>();
    }
}
