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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class OAuthUser extends Auditable {

    private String provider;
    private String providerUserId;
    private String profilePicture;
    private boolean emailVerified;
	
	@OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}