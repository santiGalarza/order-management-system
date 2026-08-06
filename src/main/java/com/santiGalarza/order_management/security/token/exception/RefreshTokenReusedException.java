package com.santiGalarza.order_management.security.token.exception;

public class RefreshTokenReusedException extends RuntimeException {
    public RefreshTokenReusedException() {
      super("Refresh token has already been used");
    }
}
