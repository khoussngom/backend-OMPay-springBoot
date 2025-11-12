package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.RefreshToken;
import com.khouss.UsersMicroservice.entities.User;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUser(User user);

    void deleteExpiredTokens();
}