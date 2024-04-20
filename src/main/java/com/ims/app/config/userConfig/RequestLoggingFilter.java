package com.ims.app.config.userConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        LOGGER.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        // Log headers
        Enumeration<String> headerNames = request.getHeaderNames();
        LOGGER.info("headerName",headerNames);
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            LOGGER.info("Header: {}={}", headerName, request.getHeader(headerName));
        }

        filterChain.doFilter(request, response);
    }


}
