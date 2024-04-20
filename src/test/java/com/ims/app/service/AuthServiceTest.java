package com.ims.app.service;

import com.ims.app.config.jwtConfig.JwtTokenGenerator;
import com.ims.app.dao.UserRepo;
import com.ims.app.dto.AuthResponseDto;
import com.ims.app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetJwtTokensWhenUserNotFound() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("nonexistent@example.com");
        when(userRepo.findByEmailId(anyString())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.getJwtTokensAfterAuthentication(authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("USER NOT FOUND ", exception.getReason());
    }

    @Test
    void testGetJwtTokensWhenJwtGenerationFails() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        User user = new User();
        when(userRepo.findByEmailId(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenGenerator.generateAccessToken(any(Authentication.class))).thenThrow(new RuntimeException("Failed to generate token"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authService.getJwtTokensAfterAuthentication(authentication)
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Please Try Again", exception.getReason());
    }

    @Test
    void testGetJwtTokensAfterAuthentication() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        User user = new User();
        when(userRepo.findByEmailId(anyString())).thenReturn(Optional.of(user));
        when(jwtTokenGenerator.generateAccessToken(any(Authentication.class))).thenReturn("jwtToken");

        AuthResponseDto response = authService.getJwtTokensAfterAuthentication(authentication);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        verify(userRepo, times(1)).findByEmailId(anyString());
        verify(jwtTokenGenerator, times(1)).generateAccessToken(any(Authentication.class));
    }
}
