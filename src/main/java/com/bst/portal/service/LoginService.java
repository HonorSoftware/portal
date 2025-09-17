package com.bst.portal.service;

import com.bst.portal.dto.LoginEntryDetails;
import com.bst.portal.dto.LoginRegistrationDetails;
import com.bst.portal.dto.LoginRequest;
import com.bst.portal.dto.LoginResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoginService {

    // Регулярное выражение для мобильных номеров
    private static final String MOBILE_PHONE_PATTERN =
            "^(\\+375)(25|29|33|44)\\d{7}$";

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final JdbcTemplate jdbcTemplate;

    public LoginService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ResponseEntity<?> processLogin(LoginRequest loginRequest) throws FileNotFoundException {
        // Валидация типа запроса
        if (loginRequest.getLoginRequestType() == null) {
            throw new IllegalArgumentException("Тип запроса обязателен");
        }

        // Обработка разных типов запросов
        return switch (loginRequest.getLoginRequestType()) {
            case "entry" -> handleEntryRequest(loginRequest);
            case "registration" -> handleRegistrationRequest(loginRequest);
            default -> throw new IllegalArgumentException("Неверный тип запроса: " + loginRequest.getLoginRequestType());
        };
    }

    private ResponseEntity<LoginResponse> handleEntryRequest(LoginRequest loginRequest) throws FileNotFoundException {
        // Проверяем наличие необходимых данных для входа
        if (loginRequest.getLoginEntryDetails() == null) {
            throw new IllegalArgumentException("Для входа требуются данные авторизации");
        }

        LoginEntryDetails details = loginRequest.getLoginEntryDetails();
        if (details.getPhoneNumber() == null || details.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Для входа требуется номер телефона");
        }

        // Бизнес-логика
        // проверяем формат номера телефона
        validatePhoneNumber(details.getPhoneNumber());

        //проверка существования пользователя. Если он существует, то для входа ОК - идем дальше
        checkUserExistence(details.getPhoneNumber());

        sendSmsCode(details.getPhoneNumber());

        LoginResponse response = new LoginResponse();
        response.setPhoneNumber(details.getPhoneNumber());
        response.setLoginRequestType(loginRequest.getLoginRequestType());

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<LoginResponse> handleRegistrationRequest(LoginRequest loginRequest) {
        // Проверяем наличие необходимых данных для регистрации
        if (loginRequest.getLoginRegistrationDetails() == null) {
            throw new IllegalArgumentException("Для регистрации требуются дополнительные данные");
        }

        LoginRegistrationDetails details = loginRequest.getLoginRegistrationDetails();

        // Валидация обязательных полей
        validateRegistrationDetails(details);

        // Бизнес-логика: проверка уникальности, сохранение данных, отправка SMS
        validatePhoneNumber(details.getPhoneNumber());

        // проверка валидности адреса электронной почты
        validateEmail(details.getEmail());

        // добавление пользователя в статусе NEW
        addNewUser(loginRequest.getLoginRegistrationDetails());

        sendSmsCode(details.getPhoneNumber());

        LoginResponse response = new LoginResponse();
        response.setPhoneNumber(details.getPhoneNumber());
        response.setLoginRequestType(loginRequest.getLoginRequestType());

        return ResponseEntity.ok(response);
    }

    private void validateRegistrationDetails(LoginRegistrationDetails details) {
        if (details.getPhoneNumber() == null || details.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Номер телефона обязателен для регистрации");
        }
        if (details.getEmail() == null || details.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email обязателен для регистрации");
        }
        if (details.getRole() == null || details.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Роль пользователя обязательна для регистрации");
        }

        // Дополнительная валидация для арендодателя
        if ("lessor".equals(details.getRole())) {
            if (details.getLessorsName() == null || details.getLessorsName().trim().isEmpty()) {
                throw new IllegalArgumentException("Название компании арендодателя обязательно");
            }
            if (details.getLessorRegion() == null || details.getLessorRegion().trim().isEmpty()) {
                throw new IllegalArgumentException("Регион арендодателя обязателен");
            }
            if (details.getLessorUnp() == null || details.getLessorUnp().trim().isEmpty()) {
                throw new IllegalArgumentException("УНП арендодателя обязателен");
            }
        }
    }

    // Методы бизнес-логики
    private void validatePhoneNumber(String phoneNumber) {
        // Реализация проверки формата номера телефона

        var isPhoneNumberValid = false;

        String cleanedNumber = phoneNumber.replaceAll("[^0-9+]", "");
        Pattern pattern = Pattern.compile(MOBILE_PHONE_PATTERN);
        Matcher matcher = pattern.matcher(cleanedNumber);

        isPhoneNumberValid = matcher.matches();

        if (!isPhoneNumberValid) {
            throw new IllegalArgumentException("Не корректный формат номера телефона");
        }
    }

    private void addNewUser(LoginRegistrationDetails loginRegistrationDetails) {
        try {
            String sql = "INSERT INTO bst.users (phone_number, role, email, lessor_name, lessor_unp, lessor_region) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, loginRegistrationDetails.getPhoneNumber(), loginRegistrationDetails.getRole(), loginRegistrationDetails.getEmail(), loginRegistrationDetails.getLessorsName(), loginRegistrationDetails.getLessorUnp(), loginRegistrationDetails.getLessorRegion());
        } catch (Exception e) {
            System.out.println("Ошибка при вставке в БД");
            throw new RuntimeException(e);
        }
    }

    private void validateEmail(String email) {
        // Реализация проверки формата email
        var isEmailValid = false;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        isEmailValid = matcher.matches();

        if (!isEmailValid) {
            throw new IllegalArgumentException("Не корректный формат адреса электронной почты");
        }
    }

    private void checkUserExistence(String phoneNumber) throws FileNotFoundException {
        // Реализация проверки существования пользователя в БД
        try {
            String sql = "SELECT COUNT(*) FROM bst.users WHERE phone_number = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phoneNumber);

            if (!(count != null && count > 0)) {
                throw new IllegalArgumentException("Пользователь не найден");
            }

        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Пользователь не найден");
        } catch (Exception e) {
            System.err.println("Error searching user: " + e.getMessage());
            throw new IllegalArgumentException("Ошибка при поиске пользователя");
        }
    }

    private void sendSmsCode(String phoneNumber) {
        // Реализация отправки SMS кода:

        // 1. генерируем случайное число из 4 цифр
        int smsCode = (int) (Math.random() * 9000) + 1000;
        // 2. сохраняем номер телефона + случайное число из 4 цифр + valid_until + created_at

        try {
            String sql = "INSERT INTO bst.sms_codes (phone_number, sms_code, valid_until) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, phoneNumber, smsCode, LocalDateTime.now().plusMinutes(5));
        } catch (Exception e) {
            System.out.println("Ошибка при попытке добавить СМС в таблицу");
            throw new RuntimeException(e);
        }
        // 3. отправляем SMS - интеграция с SMSp.by

        System.out.println("отправили СМС: " + smsCode);
    }
}
