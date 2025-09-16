package com.bst.portal.dto;

public class LoginRegistrationDetails {
    private String phoneNumber;
    private String email;
    private String role;
    private String lessorsName;
    private String lessorRegion;
    private String lessorUnp;

    // Геттеры и сеттеры
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getLessorsName() {
        return lessorsName;
    }

    public void setLessorsName(String lessorsName) {
        this.lessorsName = lessorsName;
    }

    public String getLessorRegion() {
        return lessorRegion;
    }

    public void setLessorRegion(String lessorRegion) {
        this.lessorRegion = lessorRegion;
    }

    public String getLessorUnp() {
        return lessorUnp;
    }

    public void setLessorUnp(String lessorUnp) {
        this.lessorUnp = lessorUnp;
    }
}
