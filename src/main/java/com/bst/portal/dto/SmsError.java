package com.bst.portal.dto;

public class SmsError {
    private String error;
    private String message;

    // конструкторы
    public SmsError() {}

    public SmsError(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // геттеры и сеттеры
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
