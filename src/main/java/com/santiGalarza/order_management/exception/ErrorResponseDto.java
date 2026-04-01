package com.santiGalarza.order_management.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        int status,
        LocalDateTime timestamp
){
}
