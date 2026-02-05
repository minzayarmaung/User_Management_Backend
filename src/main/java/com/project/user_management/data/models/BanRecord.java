package com.project.user_management.data.models;

import com.project.user_management.data.common.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BanRecord extends Auditable {
    private String description;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id", unique = true, nullable = false)
    private User bannedBy;
}
