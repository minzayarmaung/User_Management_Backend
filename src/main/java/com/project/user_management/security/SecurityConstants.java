package com.project.user_management.security;

public class SecurityConstants {
    public static final String[] WHITELIST = {
            // Authentication & OAuth
            "/user-management/api/v1/auth/users/signup",
            "/user-management/api/v1/auth/users/login",
            "/user-management/api/v1/auth/login",
            "/api/v1/auth/users/login",
            "api/v1/auth/users/signup",

            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources"
    };

    private SecurityConstants(){

    }
}
