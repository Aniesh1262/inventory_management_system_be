package com.ims.app.dao;

import com.ims.app.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenBlackListRepo extends JpaRepository<TokenBlackList,Long> {
    Optional<TokenBlackList> findByToken(String token);
    TokenBlackList findByTokenAndIsBlackListedTrue(String token);
}
