package com.project.user_management.features.users.service;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.features.users.dto.request.LoginRequest;
import com.project.user_management.features.users.dto.request.SignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ApiResponse registerUser(@Valid SignupRequest signupRequest, HttpServletResponse httpResponse);

    ApiResponse refreshToken(HttpServletRequest request, HttpServletResponse httpResponse);

    ApiResponse loginUser(LoginRequest loginRequest, HttpServletResponse httpResponse);

    ApiResponse getCurrentUserInfo(HttpServletRequest request);
}
