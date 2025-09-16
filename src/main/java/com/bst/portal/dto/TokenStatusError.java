package com.bst.portal.dto;

public class TokenStatusError {
    private String error;
    private String message;

    // Конструкторы
    public TokenStatusError() {
    }

    public TokenStatusError(String error, String message) {
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
