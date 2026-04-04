package com.finance.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.dto.request.UpdateUserRoleRequest;
import com.finance.dto.request.UpdateUserStatusRequest;
import com.finance.dto.request.UserRequest;
import com.finance.dto.response.UserResponse;
import com.finance.exception.AccessDeniedException;
import com.finance.exception.ConflictException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.mapper.UserMapper;
import com.finance.model.UserEntity;
import com.finance.model.enums.Role;
import com.finance.model.enums.UserStatus;
import com.finance.repository.UserRepository;
import com.finance.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse createUser(UserRequest request) {
    	
    	if (repository.findByEmailAndDeletedFalse(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already exists");
        }
    	
        UserEntity user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword())); 
        user.setStatus(UserStatus.ACTIVE);
        return mapper.toResponse(repository.save(user));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return repository.findAllByDeletedFalse()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserEntity userEntity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapper.toResponse(userEntity);
    }

    @Override
    public UserResponse updateUserRole(Long id, UpdateUserRoleRequest request) {
        UserEntity userEntity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userEntity.setRole(request.getRole());
        return mapper.toResponse(repository.save(userEntity));
    }

    @Override
    public UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        UserEntity userEntity = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userEntity.setStatus(request.getStatus());
        return mapper.toResponse(repository.save(userEntity));
    }
    
    
    @Override
    public void deleteUser(Long id) {

        UserEntity loggedUser = getLoggedUser();

        if (!loggedUser.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("Only admin can delete users");
        }

        UserEntity user = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isAdmin(loggedUser)
                && !user.getId().equals(loggedUser.getId())) {
            throw new AccessDeniedException("Not allowed");
        }
        
        user.setDeleted(true);
        repository.save(user);
    }

    // ========== IS ADMIN CHECK ==========
    private boolean isAdmin(UserEntity user) {
        return user.getRole() == Role.ADMIN;
    }

	private UserEntity getLoggedUser() {
    String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    return repository.findByEmailAndDeletedFalse(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
}

}