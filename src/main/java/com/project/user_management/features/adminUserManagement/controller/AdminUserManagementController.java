package com.project.user_management.features.adminUserManagement.controller;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.common.response.utils.ResponseUtils;
import com.project.user_management.features.adminUserManagement.dto.request.CreateUserRequest;
import com.project.user_management.features.adminUserManagement.service.AdminUserManagementService;
import com.project.user_management.features.users.dto.request.SignupRequest;
import com.project.user_management.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/admin/userManagement")
@Tag(name = "User Management by Admin", description = "Endpoints for managing users for Admin")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;
    private final JWTUtil jwtUtil;

    @Operation(
            summary = "Create User",
            description = "Create User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Create User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User Created successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )

    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse httpResponse) {
        String token = jwtUtil.extractTokenFromRequest(request);
        final ApiResponse response = adminUserManagementService.createUser(token ,createUserRequest , httpResponse);
        return ResponseUtils.buildResponse(request , response);
    }
}
