package com.ims.app.service;

public interface TokenBlackListService {
    void addToBlacklist(String token);

    boolean isBlacklisted(String token);
}
