package com.ims.app.service;

import com.ims.app.dto.UserSignUp;
import com.ims.app.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Long saveUser(UserSignUp userSignUp);

    Optional<User> getUserDetails(String email);
    Optional<User> updateUserDetails(String firstName, String lastName, String email, String mobileNumber, MultipartFile profilePic);
}
