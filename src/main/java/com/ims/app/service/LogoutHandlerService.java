package com.ims.app.service;

import com.ims.app.config.userConfig.RSAKeyRecord;
import com.ims.app.dao.TokenBlackListRepo;
import com.ims.app.dto.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutHandlerService implements LogoutHandler {
    private final RSAKeyRecord rsaKeyRecord;
   private final TokenBlackListService tokenBlackLstService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("Hello");
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(!authHeader.startsWith(TokenType.Bearer.name())){
            return;
        }
        log.info("hi");

        final String token = authHeader.substring(7);
        if (tokenBlackLstService.isBlacklisted(token)){
            log.info("Token filter");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            try {
                response.getWriter().write("Not a Valid Request");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        tokenBlackLstService.addToBlacklist(token);


    }
}
