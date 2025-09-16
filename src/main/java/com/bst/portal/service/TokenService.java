package com.bst.portal.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class TokenService {

    private final JdbcTemplate jdbcTemplate;

    public TokenService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean validateToken(String token) {

        try {
            String sql = "SELECT COUNT(*) FROM bst.tokens WHERE token = ? AND valid_until > current_timestamp";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, token);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            return false;
        }
    }
}
