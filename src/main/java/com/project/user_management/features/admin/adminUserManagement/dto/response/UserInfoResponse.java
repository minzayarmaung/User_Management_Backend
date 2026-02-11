package com.project.user_management.features.admin.adminUserManagement.dto.response;

import lombok.Builder;

@Builder
public record UserInfoResponse(
        Long userId,
        String username,
        String email,
        Long roleId,
        String role
) {
}