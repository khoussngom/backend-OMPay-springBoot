package com.khouss.UsersMicroservice.services.impl;

import com.khouss.UsersMicroservice.entities.RefreshToken;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.exception.ResourceNotFoundException;
import com.khouss.UsersMicroservice.repo.RefreshTokenRepository;
import com.khouss.UsersMicroservice.services.RefreshTokenService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME:604800000}") // 7 days in milliseconds
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        
        refreshTokenRepository.deleteByUser(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusSeconds(refreshTokenExpirationMs / 1000);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setCreatedDate(now);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new ResourceNotFoundException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteExpiredTokens(now);
    }
}