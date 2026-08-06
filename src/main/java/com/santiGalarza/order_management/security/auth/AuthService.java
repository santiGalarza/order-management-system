package com.santiGalarza.order_management.security.auth;

import com.santiGalarza.order_management.security.*;
import com.santiGalarza.order_management.security.auth.dto.AuthResponse;
import com.santiGalarza.order_management.security.auth.dto.LoginRequest;
import com.santiGalarza.order_management.security.auth.dto.RegisterRequest;
import com.santiGalarza.order_management.security.auth.exception.EmailAlreadyExistsException;
import com.santiGalarza.order_management.security.token.RefreshTokenService;
import com.santiGalarza.order_management.security.token.dto.RefreshRequest;
import com.santiGalarza.order_management.security.token.dto.RefreshTokenData;
import com.santiGalarza.order_management.user.*;
import com.santiGalarza.order_management.user.exception.UserNotFoundException;
import com.santiGalarza.order_management.user.role.Role;
import com.santiGalarza.order_management.user.role.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil, RefreshTokenService refreshTokenService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER not found — check your seed data"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.generate(user, null);
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.generate(user, request.getDeviceId());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshTokenData data = refreshTokenService.peek(request.getRefreshToken());

        User user = userRepository.findById(data.userId())
                .orElseThrow(() -> new UserNotFoundException(data.userId()));

        refreshTokenService.validate(request.getRefreshToken(), user);
        refreshTokenService.revoke(request.getRefreshToken(), user.getId());

        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = refreshTokenService.generate(user, data.deviceId());
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
    public void logout(RefreshRequest request) {
        RefreshTokenData data = refreshTokenService.peek(request.getRefreshToken());
        refreshTokenService.revoke(request.getRefreshToken(), data.userId());
    }}
