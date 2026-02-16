package com.project.user_management.features.admin.adminUserManagement.service.serviceImpl;

import com.project.user_management.common.constant.Status;
import com.project.user_management.common.exceptions.BadRequestException;
import com.project.user_management.common.exceptions.DuplicateEntityException;
import com.project.user_management.common.exceptions.UnauthorizedException;
import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.common.response.dto.PaginatedApiResponse;
import com.project.user_management.common.response.dto.PaginationMeta;
import com.project.user_management.common.util.AuditHelper;
import com.project.user_management.data.enums.ROLE;
import com.project.user_management.data.enums.UserRoleFilter;
import com.project.user_management.data.enums.UserStatusFilter;
import com.project.user_management.data.models.BanRecord;
import com.project.user_management.data.models.Role;
import com.project.user_management.data.models.User;
import com.project.user_management.data.respositories.AdminUserManagementRepository;
import com.project.user_management.data.respositories.BanRecordRepository;
import com.project.user_management.data.respositories.RoleRepository;
import com.project.user_management.data.respositories.UserRepository;
import com.project.user_management.features.admin.adminUserManagement.dto.request.CreateUserRequest;
import com.project.user_management.features.admin.adminUserManagement.dto.request.UpdateUserRequest;
import com.project.user_management.features.admin.adminUserManagement.dto.response.CreateUserResponse;
import com.project.user_management.features.admin.adminUserManagement.dto.response.UpdateUserResponse;
import com.project.user_management.features.admin.adminUserManagement.dto.response.UserListResponse;
import com.project.user_management.features.admin.adminUserManagement.dto.response.UserResponse;
import com.project.user_management.features.admin.adminUserManagement.mapper.AdminUserManagementMapper;
import com.project.user_management.features.admin.adminUserManagement.service.AdminUserManagementService;
import com.project.user_management.security.JWT.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserManagementServiceImpl implements AdminUserManagementService {

    private final AdminUserManagementRepository adminUserManagementRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JWTUtil jwtUtil;
    private final AuditHelper auditHelper;
    private final BanRecordRepository banRecordRepository;


    @Override
    public ApiResponse createUser(String token , CreateUserRequest createUserRequest, HttpServletResponse httpResponse) {
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found from token"));

        // Only Admin can create users
        if (!user.getRole().getName().equals(ROLE.ADMIN.name())) {
            throw new UnauthorizedException("Unauthorized Request");
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
        Role userRole = roleRepository.findByName(createUserRequest.role())
                .orElseThrow(() -> new RuntimeException(createUserRequest.role() +" role not found"));
        newUser.setRole(userRole);
        User savedUser = userRepository.save(newUser);

        CreateUserResponse createUserResponse = AdminUserManagementMapper.mapUserToCreateUserResponse(savedUser);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.CREATED.value())
                .message("User Created Successfully.")
                .data(createUserResponse)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse updateUser(Long userId , UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Does Not Exist."));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (updateUserRequest.role() != null &&
                user.getEmail().equals(auth.getName()) &&
                !updateUserRequest.role().equals(ROLE.ADMIN.name())) {
            throw new BadRequestException("Admin cannot change their own role");
        }
        if (updateUserRequest.username() != null) {
            user.setUsername(updateUserRequest.username());
        }
        if (updateUserRequest.email() != null &&
                !updateUserRequest.email().equals(user.getEmail()) &&
                userRepository.existsByEmail(updateUserRequest.email())) {
            throw new DuplicateEntityException("Email already exists.");
        }
        if (updateUserRequest.email() != null) {
            user.setEmail(updateUserRequest.email());
        }
        if (updateUserRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.password()));
        }
        if (updateUserRequest.role() != null) {
            Role newRole = roleRepository.findByName(updateUserRequest.role())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found"));
            user.setRole(newRole);
        }

        User updatedUser =userRepository.save(user);

        UpdateUserResponse updateUserResponse = AdminUserManagementMapper.mapUserToUpdateUserResponse(updatedUser);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("User updated successfully.")
                .data(updateUserResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse banUser(Long id, String description, HttpServletRequest request) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        String token = jwtUtil.extractTokenFromRequest(request);
        User currentLogin = auditHelper.getUserFromToken(token);

        if (currentLogin.getId().equals(targetUser.getId())) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("You cannot ban yourself.")
                    .data(false)
                    .build();
        }

        boolean targetIsAdmin = targetUser.getRole() != null
                && ROLE.ADMIN.name().equals(targetUser.getRole().getName());

        if (targetIsAdmin) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("You cannot ban another admin.")
                    .data(false)
                    .build();
        }

        banRecordRepository.save(
                BanRecord.builder()
                        .user(targetUser)
                        .bannedBy(currentLogin)
                        .description(description)
                        .build());

        targetUser.setStatus(Status.INACTIVE);
        userRepository.save(targetUser);
        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("User banned successfully.")
                .data(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedApiResponse<UserListResponse> getAllUsers(
            String keyword,
            UserRoleFilter roleFilter,
            UserStatusFilter statusFilter,
            Pageable pageable
    ) {

        Specification<User> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 🔹 Role Filtering
            switch (roleFilter) {
                case ONLY_ADMINS ->
                        predicates.add(cb.equal(root.get("role").get("name"), ROLE.ADMIN.name()));

                case ONLY_USERS ->
                        predicates.add(cb.notEqual(root.get("role").get("name"), ROLE.ADMIN.name()));

                case ALL -> {
                    // do nothing
                }
            }

            // 🔹 Status Filtering
            switch (statusFilter) {
                case ACTIVE_ONLY ->
                        predicates.add(cb.notEqual(root.get("status"), Status.INACTIVE));

                case BANNED_ONLY ->
                        predicates.add(cb.equal(root.get("status"), Status.INACTIVE));

                case ALL -> {
                    // do nothing
                }
            }

            // 🔹 Keyword Search
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), likePattern),
                        cb.like(cb.lower(root.get("email")), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserListResponse> userResponses =
                AdminUserManagementMapper.mapUserListResponse(userPage);

        PaginationMeta meta = PaginationMeta.builder()
                .totalItems(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

        return PaginatedApiResponse.<UserListResponse>builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("Users retrieved successfully.")
                .meta(meta)
                .data(userResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        UserResponse response = AdminUserManagementMapper.mapUserResponse(user);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("User retrieved successfully.")
                .data(response)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse reactivateUser(Long id, HttpServletRequest request) {

        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        String token = jwtUtil.extractTokenFromRequest(request);
        User currentLogin = auditHelper.getUserFromToken(token);

        if (currentLogin.getId().equals(targetUser.getId())) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("You cannot reactivate yourself.")
                    .data(false)
                    .build();
        }

        boolean targetIsAdmin = targetUser.getRole() != null
                && ROLE.ADMIN.name().equals(targetUser.getRole().getName());

        if (targetIsAdmin) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.FORBIDDEN.value())
                    .message("Admin users cannot be reactivated.")
                    .data(false)
                    .build();
        }

        BanRecord banRecord = targetUser.getBanRecord();
        if (banRecord == null || targetUser.getStatus() != Status.INACTIVE) {
            return ApiResponse.builder()
                    .success(0)
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("User is not banned.")
                    .data(false)
                    .build();
        }

        targetUser.setBanRecord(null);
        targetUser.setStatus(Status.ACTIVE);
        userRepository.save(targetUser);

        return ApiResponse.builder()
                .success(1)
                .code(HttpStatus.OK.value())
                .message("User reactivated successfully.")
                .data(true)
                .build();
    }
}
