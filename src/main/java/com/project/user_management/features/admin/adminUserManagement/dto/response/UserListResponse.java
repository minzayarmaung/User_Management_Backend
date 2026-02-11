package com.project.user_management.features.admin.adminUserManagement.dto.response;

import lombok.Builder;

@Builder
public record UserListResponse(
        Long userId,
        String email,
        String profilePictureUrl,
        String username,
        String status,
        String role
) { }
