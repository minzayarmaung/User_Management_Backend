package com.project.user_management.features.adminUserManagement.controller;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.common.response.dto.PaginatedApiResponse;
import com.project.user_management.common.response.utils.ResponseUtils;
import com.project.user_management.features.adminUserManagement.dto.request.CreateUserRequest;
import com.project.user_management.features.adminUserManagement.dto.request.UpdateUserRequest;
import com.project.user_management.features.adminUserManagement.dto.response.UserListResponse;
import com.project.user_management.features.adminUserManagement.service.AdminUserManagementService;
import com.project.user_management.features.users.dto.request.SignupRequest;
import com.project.user_management.security.JWT.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists.")
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

    @Operation(
            summary = "Update User",
            description = "Update User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User Update successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists.")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@Parameter(description = "User ID", required = true) @PathVariable Long userId,
                                                  @Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                  HttpServletRequest request) {
        final ApiResponse response = adminUserManagementService.updateUser(userId ,updateUserRequest);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Ban user by User ID",
            description = "Ban a user by its ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User Banned successfully")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/ban/{id}")
    public ResponseEntity<ApiResponse> banUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long id,
            @RequestParam(name = "desc") @NotBlank String description,
            HttpServletRequest httpRequest
    ) {
        final ApiResponse response = adminUserManagementService.banUser(id,description,httpRequest);
        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @Operation(
            summary = "Get all users with pagination",
            description = "Retrieve all users with pagination and optional keyword search",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully")
            }
    )

    @GetMapping
    public ResponseEntity<PaginatedApiResponse<UserListResponse>> getAllUsers(
            @Parameter(description = "Search keyword")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir,
            @Parameter(description = "Include banned (inactive) users")
            @RequestParam(defaultValue = "false") Boolean includeBanUsers,
            @Parameter(description = "Include admin users")
            @RequestParam(defaultValue = "false") Boolean includeAdmins
            ) {

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PaginatedApiResponse<UserListResponse> response = adminUserManagementService.getAllUsers(keyword, includeAdmins, includeBanUsers, pageable);
        return ResponseEntity.ok(response);
    }
}
