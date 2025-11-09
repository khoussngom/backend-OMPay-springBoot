package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;


@Tag(name = "Authentication", description = "Endpoints for user authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(
            summary = "User Login",
            description = "Authenticates a user and returns a JWT token upon successful login."
    )
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = jwtUtil.generateToken(username);
            response.put("token", token);
            response.put("username", username);
            return response;
        } catch (AuthenticationException e) {
            response.put("error", "Email ou mot de passe incorrect !");
            return response;
        }
    }
}
