package com.example.chatroom.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "test-secret-key-must-be-at-least-256-bits-long-for-hmac-sha");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    void generateToken_shouldContainUsername() {
        String token = jwtService.generateToken("alice");
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        String token = jwtService.generateToken("alice");
        UserDetails userDetails = User.withUsername("alice").password("pass").roles("USER").build();
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_forWrongUser() {
        String token = jwtService.generateToken("alice");
        UserDetails otherUser = User.withUsername("bob").password("pass").roles("USER").build();
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String token = jwtService.generateToken("alice");
        UserDetails userDetails = User.withUsername("alice").password("pass").roles("USER").build();
        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }
}
