package com.project.user_management.common.util;

import com.project.user_management.data.common.Auditable;
import com.project.user_management.data.models.User;
import com.project.user_management.data.respositories.UserRepository;
import com.project.user_management.security.JWT.JWTUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class AuditHelper {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     * Sets the audit field (createdBy, lastModifiedBy, or deletedBy) on an Auditable entity from a JWT token.
     * 
     * @param auditable The Auditable entity to set the field on
     * @param fieldName The name of the field to set (e.g., "createdBy", "lastModifiedBy", "deletedBy")
     * @param token The JWT token to extract the user from
     * @throws EntityNotFoundException if the user is not found
     * @throws IllegalArgumentException if the field name is invalid
     */
    public void setAuditFieldFromToken(Auditable auditable, String fieldName, String token) {
        if (auditable == null || fieldName == null || token == null) {
            throw new IllegalArgumentException("Auditable entity, field name, and token cannot be null");
        }

        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));

        setAuditField(auditable, fieldName, user);
    }

    /**
     * Sets the audit field (createdBy, lastModifiedBy, or deletedBy) on an Auditable entity with a User object.
     * 
     * @param auditable The Auditable entity to set the field on
     * @param fieldName The name of the field to set (e.g., "createdBy", "lastModifiedBy", "deletedBy")
     * @param user The User object to set
     * @throws IllegalArgumentException if the field name is invalid
     */
    public void setAuditField(Auditable auditable, String fieldName, User user) {
        if (auditable == null || fieldName == null) {
            throw new IllegalArgumentException("Auditable entity and field name cannot be null");
        }

        try {
            // Capitalize first letter for setter method name
            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            
            // Get the setter method
            Method setter = auditable.getClass().getMethod(setterName, User.class);
            
            // Invoke the setter
            setter.invoke(auditable, user);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName + ". Must be one of: createdBy, lastModifiedBy, deletedBy", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set audit field: " + fieldName, e);
        }
    }

    /**
     * Gets the current user from a JWT token.
     * 
     * @param token The JWT token
     * @return The User object
     * @throws EntityNotFoundException if the user is not found
     */
    public User getUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
    }
}
