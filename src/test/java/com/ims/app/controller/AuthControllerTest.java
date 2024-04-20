package com.ims.app.controller;

import com.ims.app.dto.AuthResponseDto;
import com.ims.app.dto.TokenType;
import com.ims.app.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setAccessToken("fakeToken");
        responseDto.setUserName("user");
        responseDto.setTokenType(TokenType.Bearer);
        responseDto.setAccessTokenExpiry(900);

        when(authService.getJwtTokensAfterAuthentication(any())).thenReturn(responseDto);
    }

    @Test
    void testAuthenticateUser() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        mockMvc.perform(post("/sign-in")
                        .principal(authentication)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("fakeToken")));

        verify(authService).getJwtTokensAfterAuthentication(any(Authentication.class));
    }
}
