package com.ims.app.service;

import com.ims.app.dao.UserRepo;
import com.ims.app.dto.UserSignUp;
import com.ims.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public Long saveUser(UserSignUp userSignUp) {
        String roles="";
        if (userSignUp.getUser().equals("User")){
            roles="ROLE_USER";
        }
        User user=new User(userSignUp.getFirstName(),
                userSignUp.getLastName(),
                userSignUp.getEmail(),
                passwordEncoder.encode(userSignUp.getPassword()),
                userSignUp.getMobileNumber(),
                roles);
        User savedUser=userRepo.save(user);
        return savedUser.getId();
    }
}
