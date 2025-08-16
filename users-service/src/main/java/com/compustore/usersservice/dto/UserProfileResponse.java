package com.compustore.usersservice.dto;

import com.compustore.usersservice.model.Role;

/**
 * DTO returned by the profile endpoint.  It exposes a subset of user fields
 * without including the password.  Only the id, username and role are
 * returned.
 */
public class UserProfileResponse {
    private Long id;
    private String username;
    private Role role;

    public UserProfileResponse(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}