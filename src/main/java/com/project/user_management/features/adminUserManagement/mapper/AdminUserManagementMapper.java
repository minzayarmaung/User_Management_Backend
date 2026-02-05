package com.project.user_management.features.adminUserManagement.mapper;

import com.project.user_management.data.models.User;
import com.project.user_management.features.adminUserManagement.dto.response.CreateUserResponse;
import com.project.user_management.features.users.dto.response.SignUpResponse;

public class AdminUserManagementMapper {
    private AdminUserManagementMapper() {
        throw new IllegalStateException("Mapper Classs");
    }

    public static CreateUserResponse mapUserToCreateUserResponse(User entity) {
        if(entity == null) return null;

        return CreateUserResponse.builder()
                .userId(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }
}
