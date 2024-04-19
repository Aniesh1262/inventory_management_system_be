package com.ims.app.controller;

import com.ims.app.dto.UserSignUp;
import com.ims.app.entity.User;
import com.ims.app.service.AuthService;
import com.ims.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication){
        log.info("auth",authentication);
        return ResponseEntity.ok(authService.getJwtTokensAfterAuthentication(authentication));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserSignUp userSignUp) {
        Long id = userService.saveUser(userSignUp);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

}
