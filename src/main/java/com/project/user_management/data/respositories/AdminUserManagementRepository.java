package com.project.user_management.data.respositories;

import com.project.user_management.data.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface AdminUserManagementRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(@NotEmpty(message = "Email is required.") @Email(message = "Email format is invalid.") String email);
}
