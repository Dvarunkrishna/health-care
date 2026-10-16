package com.healthcare.dto;

import com.healthcare.entity.Role;

public class AuthResponse {

    private String token;
    private String email;
    private String role;
    private String fullName;
    private Long userId;

    public AuthResponse() {
    }

    public AuthResponse(String token, String email, String role, String fullName, Long userId) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRole(Role role) {
        this.role = role == null ? null : role.name();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}