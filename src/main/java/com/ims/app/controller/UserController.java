package com.ims.app.controller;

import com.ims.app.entity.User;
import com.ims.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    @GetMapping("/getUserDetails")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        String email= authentication.getName();
        Optional<User> user= userService.getUserDetails(email);
        return ResponseEntity.ok(user);


    }
    @PutMapping("/updateUserDetails")
    public ResponseEntity<?> updateUserDetails(Authentication authentication ,@RequestParam("firstName") String firstName,
                                               @RequestParam("lastName") String lastName,

                                               @RequestParam("mobileNumber") String mobileNumber,
                                               @RequestParam(value = "profilePic", required = false) MultipartFile profilePic){

        String email= authentication.getName();
        Optional<User> user=userService.updateUserDetails(firstName,lastName,email,mobileNumber,profilePic);
        return ResponseEntity.ok(user);
    }
}
