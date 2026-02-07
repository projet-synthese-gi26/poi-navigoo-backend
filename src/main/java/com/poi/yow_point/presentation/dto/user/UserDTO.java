package com.poi.yow_point.presentation.dto.user;

import com.poi.yow_point.application.model.Role;
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
public class UserDTO {

    private UUID userId;
    private UUID organizationId;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private Boolean isActive;
    private Boolean emailVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastLoginAt;
}
