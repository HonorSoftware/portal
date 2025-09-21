package com.bst.portal.service;

import com.bst.portal.dto.CurrentOrderError;
import com.bst.portal.dto.CurrentOrderResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public CurrentOrderResponse getOrderById(Integer orderId) {
        // Здесь должна быть логика получения заказа из базы данных
        // Это примерная реализация

        CurrentOrderResponse response = new CurrentOrderResponse();
        response.setOrderId(987654);

        return response;
    }

}
