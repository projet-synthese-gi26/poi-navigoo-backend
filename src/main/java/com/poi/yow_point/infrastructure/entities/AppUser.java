package com.poi.yow_point.infrastructure.entities;

import com.poi.yow_point.application.model.Role;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("app_user")
public class AppUser {

    @Id
    @Column("user_id")
    private UUID userId;

    @Column("organization_id")
    private UUID organizationId;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("password_hash")
    private String passwordHash;

    @Column("role")
    private Role role;

    @Column("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreatedDate
    @Column("created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

}
