package com.project.user_management.features.admin.adminUserManagement.dto.response;

import lombok.Builder;

@Builder
public record UpdateUserResponse(
        Long userId,
        String username,
        String email,
        String role
) {
}
