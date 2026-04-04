package com.finance.service;

import java.util.List;

import com.finance.dto.request.UpdateUserRoleRequest;
import com.finance.dto.request.UpdateUserStatusRequest;
import com.finance.dto.request.UserRequest;
import com.finance.dto.response.UserResponse;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUserRole(Long id, UpdateUserRoleRequest request);

    UserResponse updateUserStatus(Long id, UpdateUserStatusRequest request);

	UserResponse createUser(UserRequest request);

	void deleteUser(Long id);

}