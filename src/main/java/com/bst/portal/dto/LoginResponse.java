package com.bst.portal.dto;

public class LoginResponse {
    private String phoneNumber;
    private String loginRequestType;

    // Геттеры и сеттеры
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLoginRequestType() {
        return loginRequestType;
    }

    public void setLoginRequestType(String loginRequestType) {
        this.loginRequestType = loginRequestType;
    }
}
