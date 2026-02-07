package com.project.user_management.features.adminUserManagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserRequest(
        String username,
        @Email(message = "Email format is invalid.")
        String email,
        String password,
        String role
) {
}
