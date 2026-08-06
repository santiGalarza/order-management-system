package com.santiGalarza.order_management.user.role.exception;

import com.santiGalarza.order_management.common.exception.ResourceNotFoundException;

public class RoleNotFoundException extends ResourceNotFoundException {
    public RoleNotFoundException(String name) {
        super("Role not found: " + name);
    }
}
