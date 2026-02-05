package com.project.user_management.features.users.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponse(
        Long userId,
        String username,
        String email
) {}
