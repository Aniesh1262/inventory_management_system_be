package com.ims.app.config.jwtConfig;

import com.ims.app.config.userConfig.RSAKeyRecord;
import com.ims.app.dto.TokenType;
import com.ims.app.service.TokenBlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final TokenBlackListService tokenBlackListService;
    private final JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            if (isBearerToken(authHeader)) {
                processToken(authHeader.substring(7), request, response, filterChain);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (JwtValidationException e) {
            handleJwtException(e, response);
        }
    }

    private boolean isBearerToken(String header) {
        return header != null && header.startsWith(TokenType.Bearer.name() + " ");
    }

    private void processToken(String token, HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (tokenBlackListService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is blacklisted");
            return;
        }

        Jwt jwtToken = jwtDecoder.decode(token);
        String userName = jwtTokenUtils.getUserName(jwtToken);

        if (StringUtils.hasText(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(userName, jwtToken, request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String userName, Jwt jwtToken, HttpServletRequest request) {
        UserDetails userDetails = jwtTokenUtils.userDetails(userName);
        if (jwtTokenUtils.isTokenValid(jwtToken, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void handleJwtException(JwtValidationException exception, HttpServletResponse response) throws IOException {
        log.error("JWT validation error: {}", exception.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT validation error: " + exception.getMessage());
    }


}
