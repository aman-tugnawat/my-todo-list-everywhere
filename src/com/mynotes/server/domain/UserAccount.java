/*******************************************************************************
 *
 * My todo list everywhere
 *
 * Copyright (C) 2011 Davy Leggieri
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Tou can try the demo at http://todo-list-everywhere.appspot.com
 *
 ******************************************************************************/



package com.mynotes.server.domain;

//~--- non-JDK imports --------------------------------------------------------

import com.mynotes.shared.UserAccountDTO;

//~--- JDK imports ------------------------------------------------------------

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(
    identityType = IdentityType.APPLICATION,
    detachable   = "true"
)
public class UserAccount {
    @Persistent(mappedBy = "userAccount")
    @Element(dependent = "true")
    private Set<Note> notes = new HashSet<Note>();

    // Choose to use this id to retrieve UserAccount
    // (For Google Auth = email but for another providers it can be a different
    // value that email)
    @Persistent
    private String authId;
    @Persistent
    private String email;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long   id;
    @Persistent
    private Date   lastActivity;
    @Persistent

    // @Extension(vendorName = "datanucleus", key = "gae.unindexed",
    // value="true")
    private String name;

    public void setBasicInfo(String authId, String name, String email) {
        this.authId = authId;
        this.name   = name;
        this.email  = email;
    }

    public void setBasicInfo(String authId) {
        this.authId = authId;
    }

    public Long getId() {
        return this.id;
    }

    public String getAuthId() {
        return this.authId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public Set<Note> getNotes() {
        return notes;
    }

    public void setNotes(Set<Note> notes) {
        this.notes = notes;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    public void removeNote(Note note) {
        this.notes.remove(note);
    }

    public static UserAccountDTO toDTO(UserAccount user) {
        if (user == null) {
            return null;
        }

        return new UserAccountDTO(user.getEmail(), user.getName());
    }
}