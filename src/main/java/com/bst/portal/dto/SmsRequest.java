package com.bst.portal.dto;

public class SmsRequest {
    private String phoneNumber;
    private Integer smsCode;
    private String smsRequestType;

    // геттеры и сеттеры
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(int smsCode) {
        this.smsCode = smsCode;
    }

    public String getSmsRequestType() {
        return smsRequestType;
    }

    public void setSmsRequestType(String smsRequestType) {
        this.smsRequestType = smsRequestType;
    }
}
