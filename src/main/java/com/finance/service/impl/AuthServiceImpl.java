package com.finance.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.finance.config.JwtUtil;
import com.finance.dto.request.LoginRequest;
import com.finance.dto.response.AuthResponse;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.UserEntity;
import com.finance.repository.UserRepository;
import com.finance.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity userEntity = repository.findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(userEntity);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .message("Login successful")
                .build();
    }
}