package com.ims.app.service;

import com.ims.app.dao.TokenBlackListRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ims.app.entity.TokenBlackList;

@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {
    private final TokenBlackListRepo tokenBlackListRepo;

    @Override
    public void addToBlacklist(String token) {
        TokenBlackList tokenBlackList = new TokenBlackList(token, true);
        tokenBlackListRepo.save(tokenBlackList);
    }

    @Override
    public boolean isBlacklisted(String token) {
        TokenBlackList blackListedToken = tokenBlackListRepo.findByTokenAndIsBlackListedTrue(token);
        return blackListedToken != null;

    }
}
