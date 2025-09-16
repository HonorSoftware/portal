package com.bst.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenStatusResponse {
    @JsonProperty("isTokenActive")
    private boolean isTokenActive;

    public TokenStatusResponse(boolean isTokenActive) {
        this.isTokenActive = isTokenActive;
    }
}