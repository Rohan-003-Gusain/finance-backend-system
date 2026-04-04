package com.finance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.finance.dto.request.UpdateUserRoleRequest;
import com.finance.dto.request.UpdateUserStatusRequest;
import com.finance.dto.request.UserRequest;
import com.finance.dto.response.UserResponse;
import com.finance.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(
    	    summary = "Create new user",
    	    description = "Only ADMIN can create users with role and status"
    	)
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return service.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @PutMapping("/{id}/role")
    public UserResponse updateRole(@PathVariable Long id, @Valid
                                  @RequestBody UpdateUserRoleRequest request) {
        return service.updateUserRole(id, request);
    }

    @PutMapping("/{id}/status")
    public UserResponse updateStatus(@PathVariable Long id, @Valid
                                    @RequestBody UpdateUserStatusRequest request) {
        return service.updateUserStatus(id, request);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

    	service.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}