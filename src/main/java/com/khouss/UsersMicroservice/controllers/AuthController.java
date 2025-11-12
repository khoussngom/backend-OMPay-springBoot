package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.dtos.UserRequest;
import com.khouss.UsersMicroservice.dtos.UserResponse;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.entities.RefreshToken;
import com.khouss.UsersMicroservice.services.UserServiceImpl;
import com.khouss.UsersMicroservice.services.OtpService;
import com.khouss.UsersMicroservice.services.RefreshTokenService;
import com.khouss.UsersMicroservice.services.BlacklistedTokenService;
import com.khouss.UsersMicroservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BlacklistedTokenService blacklistedTokenService;

    public AuthController(UserServiceImpl userService, OtpService otpService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, BlacklistedTokenService blacklistedTokenService) {
        this.userService = userService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.blacklistedTokenService = blacklistedTokenService;
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String telephone, @RequestParam String otp) {
        User u = userService.FindByTelephone(telephone);
        if (u == null) return ResponseEntity.badRequest().body(Map.of("error","User not found"));
        boolean ok = otpService.validateOtp(u, otp);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error","Invalid or expired OTP"));
        // enable user and generate jwt
        u.setEnabled(true);
        try {
            userService.saveUser(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to enable user: " + e.getMessage()));
        }
        return generateTokensResponse(u);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String telephone, @RequestParam String password) {
        User u = userService.connexion(telephone, password);
        if (u == null) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        if (Boolean.FALSE.equals(u.getEnabled())) return ResponseEntity.status(403).body(Map.of("error","Account not verified. Please verify OTP."));
        return generateTokensResponse(u);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            RefreshToken token = refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));

            User user = token.getUser();
            return generateTokensResponse(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader, Authentication authentication) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            Date expiryDate = jwtUtil.getExpirationDateFromToken(accessToken);
            blacklistedTokenService.blacklistToken(accessToken, expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            // Also delete refresh tokens for the user
            User user = (User) authentication.getPrincipal();
            refreshTokenService.deleteByUser(user);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private ResponseEntity<?> generateTokensResponse(User user) {
        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        Date accessTokenExpiry = jwtUtil.getExpirationDateFromToken(accessToken);

        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken.getToken(),
            "accessTokenExpiry", accessTokenExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            "user", user
        ));
    }
}

