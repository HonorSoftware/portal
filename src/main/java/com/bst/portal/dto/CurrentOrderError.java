package com.bst.portal.dto;

public class CurrentOrderError {

    private String error;
    private String message;

    public CurrentOrderError() {}

    public CurrentOrderError(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // Геттеры и сеттеры
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
