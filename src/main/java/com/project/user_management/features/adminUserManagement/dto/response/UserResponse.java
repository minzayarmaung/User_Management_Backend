package com.project.user_management.features.adminUserManagement.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String profilePicUrl
) {
}
