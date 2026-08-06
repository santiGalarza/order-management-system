package com.santiGalarza.order_management.security.auth;

import com.santiGalarza.order_management.security.auth.dto.AuthResponse;
import com.santiGalarza.order_management.security.auth.dto.LoginRequest;
import com.santiGalarza.order_management.security.auth.dto.RegisterRequest;
import com.santiGalarza.order_management.security.token.dto.RefreshRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request);
        ResponseEntity.noContent().build();
    }
}
