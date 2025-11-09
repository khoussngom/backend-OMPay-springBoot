package com.khouss.UsersMicroservice.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${SECRET_KEY:MarakhibSecretKeyForJwtTokenGeneration2025}")
    private String SECRET;

    @Value("${EXPIRATION_TIME:360000}")
    private long EXPIRATION_TIME;

    private Key key;
    private long expirationMs;

    @PostConstruct
    public void init() {


        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
        this.expirationMs = EXPIRATION_TIME;
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
}
