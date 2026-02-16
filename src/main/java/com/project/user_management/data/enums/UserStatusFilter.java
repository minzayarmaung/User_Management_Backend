package com.project.user_management.data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatusFilter {
    ALL,
    ACTIVE_ONLY,
    BANNED_ONLY
}
