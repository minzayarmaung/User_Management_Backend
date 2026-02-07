package com.project.user_management.features.users.mapper;

import com.project.user_management.data.models.User;
import com.project.user_management.features.users.dto.response.LoginResponse;
import com.project.user_management.features.users.dto.response.SignUpResponse;

public class UserMapper {
    private UserMapper() {
        throw new IllegalStateException("Mapper Classs");
    }

    public static SignUpResponse mapUserToSignUpResponse(User entity) {
        if(entity == null) return null;

        return SignUpResponse.builder()
                .userId(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static LoginResponse mapUserToLogInResponse(User entity) {
        if(entity == null) return null;
        return LoginResponse.builder()
                .userId(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .role(entity.getRole().getName())
                .profilePicUrl(entity.getProfilePicUrl())
                .build();
    }
}
