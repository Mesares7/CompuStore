package com.compustore.usersservice.dto;

/**
 * Simple DTO used to return the generated JWT to clients.  Exposing the
 * token field as public allows Spring to automatically serialise it to JSON.
 */
public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}