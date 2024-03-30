package com.ims.app.controller;

import com.ims.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication){
        log.info("auth",authentication);
        System.out.print("Hi");
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication));
    }

}
