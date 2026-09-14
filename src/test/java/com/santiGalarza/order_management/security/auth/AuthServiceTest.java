package com.santiGalarza.order_management.security.auth;

import com.santiGalarza.order_management.security.config.JwtUtil;
import com.santiGalarza.order_management.security.auth.dto.AuthResponse;
import com.santiGalarza.order_management.security.auth.dto.LoginRequest;
import com.santiGalarza.order_management.security.auth.dto.RegisterRequest;
import com.santiGalarza.order_management.security.auth.exception.EmailAlreadyExistsException;
import com.santiGalarza.order_management.security.token.RefreshTokenService;
import com.santiGalarza.order_management.security.token.dto.RefreshRequest;
import com.santiGalarza.order_management.security.token.dto.RefreshTokenData;
import com.santiGalarza.order_management.security.token.exception.InvalidRefreshTokenException;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserRepository;
import com.santiGalarza.order_management.user.exception.UserNotFoundException;
import com.santiGalarza.order_management.user.role.Role;
import com.santiGalarza.order_management.user.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "encoded-password", "First", "Last");
        user.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("registers the user and returns tokens")
        void registersUserAndReturnsTokens() {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("new@email.com");
            request.setPassword("raw-password");
            request.setFirstName("New");
            request.setLastName("User");

            Role userRole = Role.of("USER", null);

            when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
            when(jwtUtil.generateToken(any(User.class))).thenReturn("access-token");
            when(refreshTokenService.generate(any(User.class), isNull())).thenReturn("refresh-token");

            AuthResponse response = authService.register(request);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(savedUser.getEmail()).isEqualTo("new@email.com");
            assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
            assertThat(savedUser.getFirstName()).isEqualTo("New");
            assertThat(savedUser.getLastName()).isEqualTo("User");
            assertThat(savedUser.getRoles()).contains(userRole);
            assertThat(response.getToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("throws when email already exists")
        void throwsWhenEmailAlreadyExists() {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("existing@email.com");

            when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(EmailAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws when default USER role is missing")
        void throwsWhenUserRoleMissing() {
            RegisterRequest request = new RegisterRequest();
            request.setEmail("new@email.com");

            when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
            when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(IllegalStateException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("authenticates and returns tokens")
        void authenticatesAndReturnsTokens() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@email.com");
            request.setPassword("raw-password");
            request.setDeviceId("device-1");

            when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(user)).thenReturn("access-token");
            when(refreshTokenService.generate(user, "device-1")).thenReturn("refresh-token");

            AuthResponse response = authService.login(request);

            verify(authenticationManager).authenticate(any());
            assertThat(response.getToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("propagates the exception when credentials are invalid")
        void propagatesWhenCredentialsInvalid() {
            LoginRequest request = new LoginRequest();
            request.setEmail("test@email.com");
            request.setPassword("wrong-password");

            doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            verify(userRepository, never()).findByEmail(any());
        }

        @Test
        @DisplayName("throws when authenticated user is not found")
        void throwsWhenUserNotFound() {
            LoginRequest request = new LoginRequest();
            request.setEmail("missing@email.com");
            request.setPassword("raw-password");

            when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("refresh")
    class Refresh {

        @Test
        @DisplayName("rotates the refresh token and returns new tokens")
        void rotatesTokenAndReturnsNewTokens() {
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("raw-refresh-token");

            RefreshTokenData data = new RefreshTokenData(user.getId(), user.getTokenVersion(), Instant.now(), "device-1");

            when(refreshTokenService.peek("raw-refresh-token")).thenReturn(data);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(user)).thenReturn("new-access-token");
            when(refreshTokenService.generate(user, "device-1")).thenReturn("new-refresh-token");

            AuthResponse response = authService.refresh(request);

            verify(refreshTokenService).validate("raw-refresh-token", user);
            verify(refreshTokenService).revoke("raw-refresh-token", user.getId());
            assertThat(response.getToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        }

        @Test
        @DisplayName("throws when the user tied to the token is not found")
        void throwsWhenUserNotFound() {
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("raw-refresh-token");

            UUID missingUserId = UUID.randomUUID();
            RefreshTokenData data = new RefreshTokenData(missingUserId, 0, Instant.now(), "device-1");

            when(refreshTokenService.peek("raw-refresh-token")).thenReturn(data);
            when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refresh(request))
                    .isInstanceOf(UserNotFoundException.class);

            verify(refreshTokenService, never()).validate(any(), any());
        }

        @Test
        @DisplayName("propagates the exception when the token is invalid")
        void propagatesWhenTokenInvalid() {
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("raw-refresh-token");

            RefreshTokenData data = new RefreshTokenData(user.getId(), 99, Instant.now(), "device-1");

            when(refreshTokenService.peek("raw-refresh-token")).thenReturn(data);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            doThrow(InvalidRefreshTokenException.class)
                    .when(refreshTokenService).validate("raw-refresh-token", user);

            assertThatThrownBy(() -> authService.refresh(request))
                    .isInstanceOf(InvalidRefreshTokenException.class);

            verify(refreshTokenService, never()).revoke(any(), any());
            verify(jwtUtil, never()).generateToken(any());
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("revokes the refresh token")
        void revokesRefreshToken() {
            RefreshRequest request = new RefreshRequest();
            request.setRefreshToken("raw-refresh-token");

            UUID userId = UUID.randomUUID();
            RefreshTokenData data = new RefreshTokenData(userId, 0, Instant.now(), "device-1");

            when(refreshTokenService.peek("raw-refresh-token")).thenReturn(data);

            authService.logout(request);

            verify(refreshTokenService).revoke("raw-refresh-token", userId);
        }
    }
}