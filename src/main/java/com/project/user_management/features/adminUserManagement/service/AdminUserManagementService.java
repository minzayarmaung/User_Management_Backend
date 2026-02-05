package com.project.user_management.features.adminUserManagement.service;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.features.adminUserManagement.dto.request.CreateUserRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AdminUserManagementService {
    ApiResponse createUser(@Valid String token , CreateUserRequest createUserRequest, HttpServletResponse httpResponse);
}
