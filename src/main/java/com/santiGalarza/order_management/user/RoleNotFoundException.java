package com.santiGalarza.order_management.user;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String name) {
        super("Role not found: " + name);
    }
}
