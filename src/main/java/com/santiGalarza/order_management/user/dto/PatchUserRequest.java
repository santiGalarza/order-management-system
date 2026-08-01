package com.santiGalarza.order_management.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchUserRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
