package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.dtos.UserRequest;
import com.khouss.UsersMicroservice.dtos.UserResponse;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.services.UserServiceImpl;
import com.khouss.UsersMicroservice.services.OtpService;
import com.khouss.UsersMicroservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserServiceImpl userService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public AuthController(UserServiceImpl userService, OtpService otpService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam String otp) {
        User u = userService.FindByUsername(username);
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
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        User u = userService.connexion(username, password);
        if (u == null) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        if (Boolean.FALSE.equals(u.getEnabled())) return ResponseEntity.status(403).body(Map.of("error","Account not verified. Please verify OTP."));
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole());
        return ResponseEntity.ok(Map.of("token", token, "user", u));
    }
}

