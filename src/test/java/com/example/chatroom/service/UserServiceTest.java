package com.example.chatroom.service;

import com.example.chatroom.dto.AuthResponse;
import com.example.chatroom.dto.LoginRequest;
import com.example.chatroom.dto.RegisterRequest;
import com.example.chatroom.exception.DuplicateResourceException;
import com.example.chatroom.model.User;
import com.example.chatroom.repository.UserRepository;
import com.example.chatroom.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @InjectMocks UserService userService;

    @Test
    void register_shouldReturnToken_whenNewUser() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("alice")).thenReturn("jwt-token");

        AuthResponse response = userService.register(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenUsernameTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("alice");
    }

    @Test
    void register_shouldThrow_whenEmailTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("alice@example.com");
    }

    @Test
    void login_shouldReturnToken_whenCredentialsValid() {
        LoginRequest req = new LoginRequest();
        req.setUsername("alice");
        req.setPassword("secret123");

        User user = User.builder().username("alice").email("alice@example.com").password("hashed").build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("alice")).thenReturn("jwt-token");

        AuthResponse response = userService.login(req);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
