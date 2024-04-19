package com.ims.app.service;

import com.ims.app.dto.UserSignUp;

public interface UserService {
    Long saveUser(UserSignUp userSignUp);
}
