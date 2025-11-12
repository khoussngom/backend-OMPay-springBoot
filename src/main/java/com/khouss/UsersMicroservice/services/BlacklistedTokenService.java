package com.khouss.UsersMicroservice.services;

import java.time.LocalDateTime;

public interface BlacklistedTokenService {

    void blacklistToken(String token, LocalDateTime expiresAt);

    boolean isTokenBlacklisted(String token);

    void cleanupExpiredTokens();
}