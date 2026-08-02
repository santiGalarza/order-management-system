package com.santiGalarza.order_management.security;

public class RefreshTokenReusedException extends RuntimeException {
    public RefreshTokenReusedException() {
      super("Refresh token has already been used");
    }
}
