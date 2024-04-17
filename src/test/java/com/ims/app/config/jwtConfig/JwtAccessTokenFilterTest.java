package com.ims.app.config.jwtConfig;


import com.ims.app.config.userConfig.RSAKeyRecord;
import com.ims.app.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAccessTokenFilterTest {

    @Mock
    private RSAKeyRecord rsaKeyRecord;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtAccessTokenFilter jwtAccessTokenFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, InvalidKeySpecException {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
        String publicKeyPEM = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtWtBrJfHZzjhtEQJ42ro" +
                "HBgsl/CXMtRPqnJmY6ul+nbgvpOFUbQakNOFJK4qBViDD9sQkAJ4uvGTcvx+jCpX" +
                "9AXTC7T2IC3pbPsjmlYnVYl2/XCaMWmoXlqJyM01FoEEkJt1eIGu9/MHgGw1lT9a" +
                "/PRyW1VQNDgwBh6BVOHAAfowcVN2XMWksSMobgN1od9LC7F7hPIl25bPALIXeA9l" +
                "QAQDVCf9Net48rJ4i7aLUVnx5IMCSDYansvWfM96wiKriP/wquJoOWz7XzkSM6mE" +
                "a+FICWn9FrKTK21S31dt6GSXoTbUlp5X2OoRbzPNQjjKX+Xsdyapft+LXn5MtHzM" +
                "rQIDAQAB";
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
        doReturn(publicKey).when(rsaKeyRecord).rsaPublicKey();
    }

    @Test
    void testAuthorizationHeaderMissing() throws ServletException, IOException {
        jwtAccessTokenFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Token abc123");
        jwtAccessTokenFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testTokenIsBlacklisted() throws ServletException, IOException {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer blacklistedToken");
        when(tokenBlackListService.isBlacklisted("blacklistedToken")).thenReturn(true);

        jwtAccessTokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testTokenDecodingFails() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalidToken");
        when(tokenBlackListService.isBlacklisted("invalidToken")).thenReturn(false);
        when(jwtDecoder.decode("invalidToken")).thenThrow(new BadJwtException("Invalid token"));
        assertThrows(BadJwtException.class, () -> jwtAccessTokenFilter.doFilterInternal(request, response, filterChain));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void testTokenIsValid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer validToken");
        when(tokenBlackListService.isBlacklisted(anyString())).thenReturn(false);
        Jwt jwt = Jwt.withTokenValue("validToken")
                .header("alg", "RS256")
                .claim("sub", "1234567890")
                .claim("name", "John Doe")
                .claim("admin", true)
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(jwtTokenUtils.getUserName(jwt)).thenReturn("validUser");
        when(jwtTokenUtils.isTokenValid(eq(jwt), any(UserDetails.class))).thenReturn(true);
        User userDetails = new User("validUser", "", AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(jwtTokenUtils.userDetails("validUser")).thenReturn(userDetails);
        jwtAccessTokenFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testTokenIsInvalid() throws ServletException, IOException {
        String malformedToken = "malformedTokenWithoutDots"; // Intentionally malformed
        request.addHeader("Authorization", "Bearer " + malformedToken);
        when(jwtDecoder.decode(malformedToken)).thenThrow(new BadJwtException("Invalid JWT serialization: Missing dot delimiter(s)"));

        assertThrows(BadJwtException.class, () -> jwtAccessTokenFilter.doFilterInternal(request, response, filterChain),
                "Expected BadJwtException for malformed JWT");

        verify(filterChain, never()).doFilter(request, response);
    }
}
