package com.project.user_management.features.admin.adminUserManagement.service;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.common.response.dto.PaginatedApiResponse;
import com.project.user_management.data.enums.UserRoleFilter;
import com.project.user_management.data.enums.UserStatusFilter;
import com.project.user_management.features.admin.adminUserManagement.dto.request.CreateUserRequest;
import com.project.user_management.features.admin.adminUserManagement.dto.request.UpdateUserRequest;
import com.project.user_management.features.admin.adminUserManagement.dto.response.UserListResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;

public interface AdminUserManagementService {
    ApiResponse createUser(String token ,@Valid  CreateUserRequest createUserRequest, HttpServletResponse httpResponse);

    ApiResponse updateUser(Long userId , @Valid UpdateUserRequest updateUserRequest);

    ApiResponse banUser(Long id, @NotBlank String description, HttpServletRequest httpRequest);

    PaginatedApiResponse<UserListResponse> getAllUsers(
            String keyword,
            UserRoleFilter roleFilter,
            UserStatusFilter statusFilter,
            Pageable pageable
    );

    ApiResponse getUserById(Long id);

    ApiResponse reactivateUser(Long id, HttpServletRequest request);
}
