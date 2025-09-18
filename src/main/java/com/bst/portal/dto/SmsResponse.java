package com.bst.portal.dto;

public class SmsResponse {
    private boolean isSmsCodeCorrect;
    private String token;

    // конструкторы
    public SmsResponse() {}

    public SmsResponse(boolean isSmsCodeCorrect, String token) {
        this.isSmsCodeCorrect = isSmsCodeCorrect;
        this.token = token;
    }

    // геттеры и сеттеры
    public boolean getIsSmsCodeCorrect() {
        return isSmsCodeCorrect;
    }

    public void setIsSmsCodeCorrect(boolean isSmsCodeCorrect) {
        isSmsCodeCorrect = isSmsCodeCorrect;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

