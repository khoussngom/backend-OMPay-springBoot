package com.khouss.UsersMicroservice.utils;

import com.khouss.UsersMicroservice.services.BlacklistedTokenService;
import com.khouss.UsersMicroservice.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final com.khouss.UsersMicroservice.security.CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenService blacklistedTokenService;

    public JwtFilter(JwtUtil jwtUtil, com.khouss.UsersMicroservice.security.CustomUserDetailsService userDetailsService, BlacklistedTokenService blacklistedTokenService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.blacklistedTokenService = blacklistedTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = null;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception ignored) {
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Check if token is blacklisted
                if (blacklistedTokenService.isTokenBlacklisted(token)) {
                    // Token is blacklisted, skip authentication
                    return;
                }

                var userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
