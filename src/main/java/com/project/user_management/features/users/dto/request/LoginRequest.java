package com.project.user_management.features.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "Email is required.")
        @Email(message = "Email format is invalid.")
        String email,
        @NotEmpty(message = "Password is required.")
        String password
) { }
