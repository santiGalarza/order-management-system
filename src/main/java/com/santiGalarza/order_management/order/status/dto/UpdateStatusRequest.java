package com.santiGalarza.order_management.order.status.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStatusRequest {

    @NotBlank
    private String statusCode;

    private String notes;
}
