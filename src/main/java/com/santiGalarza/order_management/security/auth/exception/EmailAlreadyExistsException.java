package com.santiGalarza.order_management.security.auth.exception;

import com.santiGalarza.order_management.common.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }

    @Override
    public String getTitle() {
        return "Email already registered";
    }
}
