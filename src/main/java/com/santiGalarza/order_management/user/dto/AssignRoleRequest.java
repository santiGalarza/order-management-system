package com.santiGalarza.order_management.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignRoleRequest {

    @NotBlank
    private String roleName;
}
