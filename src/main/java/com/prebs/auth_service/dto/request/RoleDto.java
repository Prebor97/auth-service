package com.prebs.auth_service.dto.request;

import com.prebs.auth_service.enums.UserRoles;
import jakarta.validation.constraints.NotBlank;

public class RoleDto {
    @NotBlank
    public UserRoles role;

    public RoleDto() {
    }

    public UserRoles getRole() {
        return role;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

}
