package com.project.user_management.features.users.service.serviceImpl;

import com.project.user_management.common.config.CookieConfig;
import com.project.user_management.common.exceptions.DuplicateEntityException;
import com.project.user_management.common.exceptions.EntityNotFoundException;
import com.project.user_management.common.response.dto.ApiResponse;
import com.project.user_management.data.models.Role;
import com.project.user_management.data.models.User;
import com.project.user_management.data.respositories.RoleRepository;
import com.project.user_management.data.respositories.UserRepository;
import com.project.user_management.features.adminUserManagement.dto.response.UserInfoResponse;
import com.project.user_management.features.users.dto.request.LoginRequest;
import com.project.user_management.features.users.dto.request.SignupRequest;
import com.project.user_management.features.users.dto.response.LoginResponse;
import com.project.user_management.features.users.dto.response.SignUpResponse;
import com.project.user_management.features.users.mapper.UserMapper;
import com.project.user_management.features.users.service.UserService;
import com.project.user_management.security.JWT.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final CookieConfig cookieConfig;


    @Override
    public ApiResponse registerUser(SignupRequest signupRequest, HttpServletResponse httpResponse) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            throw new DuplicateEntityException("Email already exists.");
        }

        // Set User Data
        User user = new User();
        user.setUsername(signupRequest.username());
        user.setEmail(signupRequest.email());
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
        user.setRole(userRole);
        User savedUser = userRepository.save(user);

        // Set Token to Cookies
        String accessToken = jwtUtil.generateToken(signupRequest.email());
        String refreshToken = jwtUtil.generateRefreshToken(signupRequest.email());
        cookieConfig.addSecureCookie(httpResponse, "accessToken", accessToken,
                (int) (jwtUtil.ACCESS_TOKEN_VALID_TIME_MILLIS() / 1000), "/");
        cookieConfig.addSecureCookie(httpResponse, "refreshToken", refreshToken,
                (int) (jwtUtil.REFRESH_TOKEN_VALID_TIME_MILLIS() / 1000), "/");

        SignUpResponse signUpResponse = UserMapper.mapUserToSignUpResponse(savedUser);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Sign Up Successfully.")
                .data(signUpResponse)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    public ApiResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ApiResponse.builder().success(0).message("Refresh Token is missing").build();
        }
        try {
            String email = jwtUtil.extractEmail(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateToken(email);
                String newRefreshToken = jwtUtil.generateRefreshToken(email);
                cookieConfig.addSecureCookie(response, "accessToken", newAccessToken,
                        (int) (jwtUtil.ACCESS_TOKEN_VALID_TIME_MILLIS() / 1000), "/");
                cookieConfig.addSecureCookie(response, "refreshToken", newRefreshToken,
                        (int) (jwtUtil.REFRESH_TOKEN_VALID_TIME_MILLIS() / 1000), "/");
                return ApiResponse.builder()
                        .success(1)
                        .code(200)
                        .message("Token Refreshed Successfully")
                        .build();
            }
        } catch (Exception e) {
            return ApiResponse.builder().success(0).message("Refresh Token Invalid or Expired").build();
        }
        return ApiResponse.builder().success(0).message("Invalid Token").build();
    }

    @Override
    public ApiResponse loginUser(LoginRequest loginRequest, HttpServletResponse httpResponse) {
        Optional<User> existingUser = userRepository.findByEmail(loginRequest.email());
        if(existingUser.isEmpty()){
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message("User does not Exist in the System.")
                    .data(loginRequest.email())
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }
        User user = existingUser.get();
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Incorrect Credentials.")
                    .meta(Map.of("timestamp", System.currentTimeMillis()))
                    .build();
        }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email() , loginRequest.password()));
        // Set Token to Cookies
        String accessToken = jwtUtil.generateToken(loginRequest.email());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequest.email());
        cookieConfig.addSecureCookie(httpResponse, "accessToken", accessToken,
                (int) (jwtUtil.ACCESS_TOKEN_VALID_TIME_MILLIS() / 1000), "/");
        cookieConfig.addSecureCookie(httpResponse, "refreshToken", refreshToken,
                (int) (jwtUtil.REFRESH_TOKEN_VALID_TIME_MILLIS() / 1000), "/");

        LoginResponse loginResponse = UserMapper.mapUserToLogInResponse(existingUser.get());

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Login Successfully.")
                .data(loginResponse)
                .meta(Map.of("timestamp", System.currentTimeMillis()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getCurrentUserInfo(HttpServletRequest request) {
        try {
            String token = jwtUtil.extractTokenFromRequest(request);

            if (token == null) {
                return ApiResponse.builder()
                        .success(0)
                        .code(401)
                        .message("Token not found. Please provide a valid token.")
                        .data(null)
                        .build();
            }

            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));

            UserInfoResponse userInfo = UserInfoResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .roleId(user.getRole() != null ? user.getRole().getId() : null)
                    .role(user.getRole() != null ? user.getRole().getName() : null)
                    .build();

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("User information retrieved successfully.")
                    .data(userInfo)
                    .build();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Token has expired. Please refresh your token.")
                    .data(null)
                    .build();
        } catch (io.jsonwebtoken.JwtException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Invalid token.")
                    .data(null)
                    .build();
        } catch (EntityNotFoundException e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(404)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(500)
                    .message("Internal server error: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);// true for production env
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
