package com.thitracnghiem.app.model;

public class UserInfo {
    private String userId;
    private String fullName;
    private String role;

    // Constructors
    public UserInfo() {}

    public UserInfo(String userId, String fullName, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
} 