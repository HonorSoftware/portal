package com.bst.portal.dto;

public class LoginRequest {
    private String loginRequestType;
    private LoginEntryDetails loginEntryDetails;
    private LoginRegistrationDetails loginRegistrationDetails;

    // Геттеры и сеттеры
    public String getLoginRequestType() {
        return loginRequestType;
    }

    public void setLoginRequestType(String loginRequestType) {
        this.loginRequestType = loginRequestType;
    }

    public LoginEntryDetails getLoginEntryDetails() {
        return loginEntryDetails;
    }

    public void setLoginEntryDetails(LoginEntryDetails loginEntryDetails) {
        this.loginEntryDetails = loginEntryDetails;
    }

    public LoginRegistrationDetails getLoginRegistrationDetails() {
        return loginRegistrationDetails;
    }

    public void setLoginRegistrationDetails(LoginRegistrationDetails loginRegistrationDetails) {
        this.loginRegistrationDetails = loginRegistrationDetails;
    }
}
