package com.bst.portal.service;

import com.bst.portal.dto.SmsRequest;
import com.bst.portal.dto.SmsResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmsService {

    private final JdbcTemplate jdbcTemplate;

    public SmsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String MOBILE_PHONE_PATTERN =
            "^(\\+375)(25|29|33|44)\\d{7}$";

    public SmsResponse verifySmsCode(SmsRequest smsRequest) {

        if (smsRequest.getSmsRequestType() == null || smsRequest.getSmsRequestType().isEmpty()) {
            throw new IllegalArgumentException("Тип запроса обязателен");
        }

        validatePhoneNumber(smsRequest.getPhoneNumber());

        // Обработка разных типов запросов
        return switch (smsRequest.getSmsRequestType()) {
            case "entry" -> handleEntrySmsRequest(smsRequest);
            case "registration" -> handleRegistrationSmsRequest(smsRequest);
            default -> throw new IllegalArgumentException("Неверный тип запроса: " + smsRequest.getSmsRequestType());
        };
    }

    public SmsResponse handleEntrySmsRequest(SmsRequest smsRequest) {
        return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), generateToken());
    }

    public SmsResponse handleRegistrationSmsRequest(SmsRequest smsRequest) {
        return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), generateToken());
    }

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

    private boolean isSmsCodeValid(int smsCode, String phoneNumber) {

        try {
            String sql = "SELECT COUNT(*) FROM bst.sms_codes WHERE sms_code = ? AND phone_number = ? AND valid_until > current_timestamp";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, smsCode, phoneNumber);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            System.err.println("Error validating SMS code: " + e.getMessage());
            return false;
        }
    }

    private String generateToken() {
        return "generated_token_" + System.currentTimeMillis();
    }

}
