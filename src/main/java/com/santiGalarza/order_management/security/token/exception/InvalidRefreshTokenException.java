package com.santiGalarza.order_management.security.token.exception;

public class InvalidRefreshTokenException extends RuntimeException{
    public InvalidRefreshTokenException() {
        super("Refresh token is invalid or has expired");
    }
}
