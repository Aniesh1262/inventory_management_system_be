package com.ims.app.config.userConfig;

import com.ims.app.dao.UserRepo;
import com.ims.app.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
@Slf4j
public class InitialUserInfo implements CommandLineRunner {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
//            User manager = new User();
//            manager.setFirstName("Manager");
//            manager.setPassword(passwordEncoder.encode("password"));
//            manager.setRoles("ROLE_MANAGER");
//            manager.setEmailId("manager@manager.com");
//
//            User admin = new User();
//            admin.setFirstName("Admin");
//            admin.setPassword(passwordEncoder.encode("password"));
//            admin.setRoles("ROLE_ADMIN");
//            admin.setEmailId("admin@admin.com");
//
//            User user = new User();
//            user.setFirstName("User");
//            user.setPassword(passwordEncoder.encode("password"));
//            user.setRoles("ROLE_USER");
//            user.setEmailId("user@user.com");
//
//            userRepo.saveAll(List.of(manager,admin,user));

    }

}
