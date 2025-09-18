package com.bst.portal.controller;

import com.bst.portal.dto.*;
import com.bst.portal.service.LoginService;
import com.bst.portal.service.SmsService;
import com.bst.portal.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final TokenService tokenService;

    private final LoginService loginService;

    private final SmsService smsService;

    @Autowired
    public AuthController(TokenService tokenService, LoginService loginService, SmsService smsService) {
        this.tokenService = tokenService;
        this.loginService = loginService;
        this.smsService = smsService;
    }

    @GetMapping("/tokenStatus/{token}")
    public ResponseEntity<?> tokenStatus(@PathVariable String token) {
        try {
            boolean isTokenActive = tokenService.validateToken(token);
            TokenStatusResponse response = new TokenStatusResponse(isTokenActive);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            TokenStatusError error = new TokenStatusError(
                    "internal_server_error",
                    "Произошла внутренняя ошибка сервера. Попробуйте позже."
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            return loginService.processLogin(loginRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new LoginError("invalid_request", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginError("internal_server_error", "Произошла внутренняя ошибка сервера. Попробуйте позже."));
        }
    }

    @PostMapping("/sms")
    public ResponseEntity<?> verifySmsCode(@RequestBody SmsRequest smsRequest) {
        try {
            SmsResponse response = smsService.verifySmsCode(smsRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new LoginError("invalid_request", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginError("internal_server_error", "Произошла внутренняя ошибка сервера. Попробуйте позже."));
        }
    }

}
