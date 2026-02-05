package com.project.user_management.features.adminUserManagement.service.serviceImpl;

import com.project.user_management.common.exceptions.DuplicateEntityException;
import com.project.user_management.common.exceptions.UnauthorizedException;
import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.data.enums.ROLE;
import com.project.user_management.data.models.Role;
import com.project.user_management.data.models.User;
import com.project.user_management.data.respositories.AdminUserManagementRepository;
import com.project.user_management.data.respositories.RoleRepository;
import com.project.user_management.data.respositories.UserRepository;
import com.project.user_management.features.adminUserManagement.dto.request.CreateUserRequest;
import com.project.user_management.features.adminUserManagement.dto.response.CreateUserResponse;
import com.project.user_management.features.adminUserManagement.mapper.AdminUserManagementMapper;
import com.project.user_management.features.adminUserManagement.service.AdminUserManagementService;
import com.project.user_management.features.users.dto.response.SignUpResponse;
import com.project.user_management.features.users.mapper.UserMapper;
import com.project.user_management.security.JWT.JWTUtil;
import io.jsonwebtoken.Jwt;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserManagementServiceImpl implements AdminUserManagementService {

    private final AdminUserManagementRepository adminUserManagementRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JWTUtil jwtUtil;

    @Override
    public ApiResponse createUser(String token , CreateUserRequest createUserRequest, HttpServletResponse httpResponse) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        // Only Admin can create users
        if (!user.getRole().getName().equals(ROLE.ADMIN.name())) {
            throw new UnauthorizedException("You do not have Permission.");
        }

        // Check duplicate email
        if (adminUserManagementRepository.existsByEmail(createUserRequest.email())) {
            throw new DuplicateEntityException("Email already exists.");
        }

        // Set User Data
        User newUser = new User();
        newUser.setUsername(createUserRequest.username());
        newUser.setEmail(createUserRequest.email());
        newUser.setPassword(passwordEncoder.encode(createUserRequest.password()));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        newUser.setRole(userRole);
        User savedUser = userRepository.save(newUser);

        CreateUserResponse createUserResponse = AdminUserManagementMapper.mapUserToCreateUserResponse(savedUser);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("User Created Successfully.")
                .data(createUserResponse)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }
}
