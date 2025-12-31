package com.poi.yow_point.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poi.yow_point.application.model.Role;
import jakarta.validation.constraints.*;
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
public class AppUserDTO {

    private UUID userId;

    @NotNull(message = "Organization ID is required")
    private UUID organizationId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, hyphens and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one lowercase letter, one uppercase letter, one digit and one special character")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    @Builder.Default
    private Boolean isActive = true;

    private OffsetDateTime createdAt;
}
