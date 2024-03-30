package com.ims.app.config.userConfig;

import com.ims.app.config.userConfig.UserInfoConfig;
import com.ims.app.dao.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagerConfig implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        return  userRepo
                .findByEmailId(emailId)
                .map(UserInfoConfig::new)
                .orElseThrow(()-> new UsernameNotFoundException("UserEmail: "+emailId+" does not exist"));
    }
    //this give the user in form of Auth object
}
