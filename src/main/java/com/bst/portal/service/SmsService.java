package com.bst.portal.service;

import com.bst.portal.dto.SmsRequest;
import com.bst.portal.dto.SmsResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
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

        String userRole = receiveRole(smsRequest.getPhoneNumber());

        boolean isSmsCodeValid = isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber());

        if (isSmsCodeValid) {
            return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), generateToken(smsRequest.getPhoneNumber(), userRole));
        } else {
            return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), null);
        }
    }

    public SmsResponse handleRegistrationSmsRequest(SmsRequest smsRequest) {

        String userRole = receiveRole(smsRequest.getPhoneNumber());

        boolean isSmsCodeValid = isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber());

        if (isSmsCodeValid) {
            updateUserStatusByPhone(smsRequest.getPhoneNumber());
            return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), generateToken(smsRequest.getPhoneNumber(), userRole));
        } else {
            return new SmsResponse(isSmsCodeValid(smsRequest.getSmsCode(), smsRequest.getPhoneNumber()), null);
        }
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

    private String receiveRole(String phoneNumber) {

        String sql = "SELECT role FROM bst.users WHERE phone_number = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{phoneNumber},
                    (rs, rowNum) -> rs.getString("role"));
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Не найден пользователь с таким номером телефона");
        }
    }

    private String generateToken(String phoneNumber, String userRole) {

        String notCodedToken = phoneNumber + "-" + userRole + "-" + generateAlphabetString(10);
        return Base64.getEncoder().encodeToString(notCodedToken.getBytes());
    }

    public String generateAlphabetString(int length) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(random.nextInt(letters.length())));
        }
        return sb.toString();
    }

    public void updateUserStatusByPhone(String phoneNumber) {
        String sql = "UPDATE bst.users SET status = 'ACTIVE' WHERE phone_number = ?";
        jdbcTemplate.update(sql, phoneNumber);
    }

}
