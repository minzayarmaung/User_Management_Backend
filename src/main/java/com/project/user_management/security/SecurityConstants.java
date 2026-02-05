package com.project.user_management.security;

public class SecurityConstants {
    public static final String[] WHITELIST = {
            // Authentication & OAuth

            // Swagger & API Docs
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/portfolio/api/v1/organization-members"
    };

    private SecurityConstants(){

    }
}
