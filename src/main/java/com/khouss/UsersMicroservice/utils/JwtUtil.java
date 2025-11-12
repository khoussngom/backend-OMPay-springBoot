package com.khouss.UsersMicroservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${SECRET_KEY:MarakhibSecretKeyForJwtTokenGeneration2025}")
    private String SECRET;

    @Value("${EXPIRATION_TIME:360000}")
    private long EXPIRATION_TIME;

    @Value("${REFRESH_TOKEN_EXPIRATION_TIME:604800000}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private Key key;
    private long expirationMs;
    private long refreshTokenExpirationMs;

    @PostConstruct
    public void init() {
        try {
            if (SECRET == null || SECRET.trim().isEmpty()) {
                logger.warn("SECRET is empty or not provided — generating random signing key");
                this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            } else {
                try {
                    byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
                    this.key = Keys.hmacShaKeyFor(keyBytes);
                } catch (Exception e) {
                    logger.warn("Failed to create HMAC key from SECRET, falling back to generated key", e);
                    this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error initializing JwtUtil key, generating fallback key", e);
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        this.expirationMs = EXPIRATION_TIME;
        this.refreshTokenExpirationMs = REFRESH_TOKEN_EXPIRATION_TIME;
        logger.info("JwtUtil initialized (expirationMs={}, refreshTokenExpirationMs={})", expirationMs, refreshTokenExpirationMs);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validerToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateToken(String token, String username) {
        if (token == null || username == null) return false;
        try {
            String extracted = extractUsername(token);
            return extracted != null && extracted.equals(username) && validerToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public String generateRefreshToken(String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
