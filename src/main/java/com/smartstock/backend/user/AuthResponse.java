package com.smartstock.backend.user;

import java.util.List;

public class AuthResponse {

    private String username;
    private List<String> roles;
    private String token;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String username, List<String> roles, String token, String message) {
        this.username = username;
        this.roles = roles;
        this.token = token;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
