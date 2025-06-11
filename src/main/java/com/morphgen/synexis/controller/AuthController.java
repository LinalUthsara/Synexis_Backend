package com.morphgen.synexis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.morphgen.synexis.security.dto.AuthRequest;
import com.morphgen.synexis.security.dto.AuthResponse;
import com.morphgen.synexis.security.jwt.JwtUtil;
import com.morphgen.synexis.security.service.MyUserDetailsService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    
    @Autowired private AuthenticationManager authManager;
    @Autowired private MyUserDetailsService userDetailsService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
}
