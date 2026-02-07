package com.project.user_management.features.users.controller;

import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.common.response.utils.ResponseUtils;
import com.project.user_management.features.users.dto.request.LoginRequest;
import com.project.user_management.features.users.dto.request.SignupRequest;
import com.project.user_management.features.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.base.path}/auth/users")
@Tag(name = "User API", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Signup User",
            description = "Signup User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Signup User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SignupRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Sign Up User successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse httpResponse) {
        final ApiResponse response = userService.registerUser(signupRequest , httpResponse);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Login User",
            description = "Login User",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login User Request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login User successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest,
                                             HttpServletRequest request, HttpServletResponse httpResponse) {
        final ApiResponse response = this.userService.loginUser(loginRequest,httpResponse);
        return ResponseUtils.buildResponse(request , response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(HttpServletRequest request,
                                                    HttpServletResponse httpResponse) {
        final ApiResponse response = userService.refreshToken(request, httpResponse);
        return ResponseUtils.buildResponse(request , response);
    }

    @Operation(
            summary = "Get Current User Information",
            description = "Retrieves the current authenticated user's information from the token in the cookie.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User information retrieved successfully.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Token not found, expired, or invalid.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUserInfo(HttpServletRequest request) {
        final ApiResponse response = this.userService.getCurrentUserInfo(request);
        return ResponseUtils.buildResponse(request, response);
    }
}
