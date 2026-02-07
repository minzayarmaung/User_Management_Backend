package com.project.user_management.features.adminUserManagement.mapper;

import com.project.user_management.data.models.User;
import com.project.user_management.features.adminUserManagement.dto.response.CreateUserResponse;
import com.project.user_management.features.adminUserManagement.dto.response.UpdateUserResponse;
import com.project.user_management.features.adminUserManagement.dto.response.UserListResponse;
import com.project.user_management.features.adminUserManagement.dto.response.UserResponse;
import com.project.user_management.features.users.dto.response.SignUpResponse;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

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
                .role(entity.getRole().getName())
                .build();
    }

    public static UpdateUserResponse mapUserToUpdateUserResponse(User entity) {
        if(entity == null) return null;

        return UpdateUserResponse.builder()
                .userId(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .role(entity.getRole().getName())
                .build();
    }

    public static List<UserListResponse> mapUserListResponse(Page<User> page) {
        if (page == null || page.isEmpty()) {
            return Collections.emptyList();
        }

        return page.getContent().stream()
                .map(AdminUserManagementMapper::mapToUserListResponse)
                .toList();
    }

    private static UserListResponse mapToUserListResponse(User user) {
        return UserListResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePicUrl())
                .username(user.getUsername())
                .status(user.getStatus().name())
                .role(user.getRole().getName())
                .build();
    }

    public static UserResponse mapUserResponse(User entity) {
        if(entity == null) return null;

        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .role(entity.getRole().getName())
                .profilePicUrl(entity.getProfilePicUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
