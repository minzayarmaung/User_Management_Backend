package com.project.user_management.features.admin.dashboard.dto.response;

import com.project.user_management.data.models.User;
import lombok.Builder;

import java.util.List;

@Builder
public record DashboardResponse(
        List<User> activeUsers,
        List<User> inActiveUsers,
        List<User> totalUsers,
        List<User> admins
) {
}
