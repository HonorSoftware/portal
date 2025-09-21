package com.bst.portal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CurrentOrderResponse {

    private Integer orderId;

    // Конструкторы, геттеры и сеттеры
    public CurrentOrderResponse() {}

    public CurrentOrderResponse(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
