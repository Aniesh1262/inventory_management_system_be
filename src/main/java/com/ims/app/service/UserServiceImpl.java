package com.ims.app.service;

import com.ims.app.dao.UserRepo;
import com.ims.app.dto.UserSignUp;
import com.ims.app.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
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

    @Override
    public  Optional<User> getUserDetails(String email) {
        Optional<User> user= userRepo.findByEmailId(email);
        return user;


    }

    @Override
    public Optional<User> updateUserDetails(String firstName, String lastName, String email, String mobileNumber, MultipartFile profilePic) {
        String imageUrl=s3Service.uploadFile(profilePic);
        Optional<User> optionalUser =userRepo.findByEmailId(email);
        User user=optionalUser.get();
        if (user!=null){
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMobileNumber(mobileNumber);
            user.setImageUrl(imageUrl);
            userRepo.save(user);
        }

        return Optional.empty();
    }
}
