package com.project.user_management.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRoleFilter {
    ALL,
    ONLY_ADMINS,
    ONLY_USERS
}
