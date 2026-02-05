package com.project.user_management.features.adminUserManagement.dto.response;

import lombok.Builder;

@Builder
public record CreateUserResponse(
        Long userId,
        String username,
        String email
) {
}
