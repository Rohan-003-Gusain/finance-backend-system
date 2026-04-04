package com.finance.service.impl;

import java.util.Optional;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.finance.exception.UnauthorizedException;
import com.finance.model.UserEntity;
import com.finance.model.enums.UserStatus;
import com.finance.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<UserEntity> userOpt = userRepository.findByEmailAndDeletedFalse(email);

        UserEntity userEntity = userOpt.orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        
        if(userEntity.getStatus() != UserStatus.ACTIVE){
            throw new UnauthorizedException("User is inactive");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().name())
                .build();
    }
}