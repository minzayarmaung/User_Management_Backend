package com.project.user_management.features.users.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        Long userId,
        String username,
        String email,
        String role,
        String profilePicUrl
) {
}
