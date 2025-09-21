package com.bst.portal.controller;

import com.bst.portal.dto.CurrentOrderError;
import com.bst.portal.dto.CurrentOrderResponse;
import com.bst.portal.service.OrderService;
import com.bst.portal.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final TokenService tokenService;
    private final OrderService orderService;

    public OrderController(TokenService tokenService, OrderService orderService) {
        this.tokenService = tokenService;
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
            @PathVariable Integer orderId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        CurrentOrderResponse response = null;

        try {
            System.out.println(authorizationHeader);

            if (authorizationHeader == null) {
                response = handleGetOrderAsNotAuthUser(orderId);
            } else {

                // Извлекаем токен из заголовка Authorization
                String token = extractTokenFromHeader(authorizationHeader);

                boolean isTokenActive = tokenService.validateToken(token);

                if (!isTokenActive) {
                    return ResponseEntity.status(401)
                            .body(new CurrentOrderError("invalid_token", "Токен недействителен или истек"));
                }

                String role = extractUserRoleFromToken(token);

                switch (role) {
                    case "lessor" -> response = handleGetOrderAsLessor(orderId);
                    case "tenant" -> response = handleGetOrderAsTenant(orderId);
                    default -> throw new IllegalArgumentException("Некорректная роль в токене");
                };

            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CurrentOrderError("internal_server_error",
                            "Произошла внутренняя ошибка сервера. Попробуйте позже."));
        }

        return ResponseEntity.ok(response);
    }

    private CurrentOrderResponse handleGetOrderAsLessor(Integer orderId) {
        return orderService.getOrderById(orderId);

    }

    private CurrentOrderResponse handleGetOrderAsTenant(Integer orderId) {

        return orderService.getOrderById(orderId);

    }

    private CurrentOrderResponse handleGetOrderAsNotAuthUser(Integer orderId) {

        return orderService.getOrderById(orderId);

    }

    private String extractTokenFromHeader(String authorizationHeader) {

        if (!(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))) {
            throw new IllegalArgumentException("Invalid Authorization header format");
        } else {
            String token = authorizationHeader.substring(7);
            return token;
        }
    }

    private String extractUserRoleFromToken(String token) {

        byte[] decodedBytes = Base64.getDecoder().decode(token);

        String unDecoded = new String(decodedBytes, StandardCharsets.UTF_8);


        if (unDecoded == null || unDecoded.trim().isEmpty()) {
            System.out.println("Входная строка пуста или null");
            throw new IllegalArgumentException("Токен неверного формата 1");
        }

        // Разделяем строку по дефисам
        String[] parts = unDecoded.split("-");

        System.out.println("Исходная строка: " + unDecoded);
        System.out.println("Количество частей после разделения: " + parts.length);

        // Извлекаем части
        if (parts.length >= 3) {
            String phoneNumber = parts[0];      // +375331234567
            String role = parts[1];         // lessor
            String postFix = parts[2];            // QHDqTEwSXc

            return role;
        } else {
            throw new IllegalArgumentException("Токен неверного формата 2");
        }
    }
}