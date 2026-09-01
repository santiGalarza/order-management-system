package com.santiGalarza.order_management;

import com.santiGalarza.order_management.security.token.RefreshTokenRepository;
import com.santiGalarza.order_management.security.token.RefreshTokenReusePolicy;
import com.santiGalarza.order_management.security.token.RefreshTokenService;
import com.santiGalarza.order_management.security.token.dto.RefreshTokenData;
import com.santiGalarza.order_management.security.token.exception.InvalidRefreshTokenException;
import com.santiGalarza.order_management.security.token.exception.RefreshTokenReusedException;
import com.santiGalarza.order_management.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenReusePolicy refreshTokenReusePolicy;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com","password" ,"First Name" ,"Last Name" );
        user.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("generate")
    class Generate {

        @Test
        @DisplayName("saves and returns a token tied to the user's id and token version")
        void savesAndReturnsToken() {
            String deviceId = "device-1";

            String token = refreshTokenService.generate(user, deviceId);

            ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<RefreshTokenData> dataCaptor = ArgumentCaptor.forClass(RefreshTokenData.class);
            verify(refreshTokenRepository).save(tokenCaptor.capture(), dataCaptor.capture());

            assertThat(tokenCaptor.getValue()).isEqualTo(token);
            assertThat(dataCaptor.getValue().userId()).isEqualTo(user.getId());
            assertThat(dataCaptor.getValue().tokenVersion()).isEqualTo(user.getTokenVersion());
            assertThat(dataCaptor.getValue().deviceId()).isEqualTo(deviceId);
        }
    }

    @Nested
    @DisplayName("validate")
    class Validate {

        @Test
        @DisplayName("returns the token data when found and token version matches")
        void returnsDataWhenTokenVersionMatches() {
            String rawToken = "raw-token-value";
            RefreshTokenData data = new RefreshTokenData(user.getId(), user.getTokenVersion(), Instant.now(), "device-1");

            when(refreshTokenRepository.find(rawToken)).thenReturn(Optional.of(data));

            RefreshTokenData result = refreshTokenService.validate(rawToken, user);

            assertThat(result).isEqualTo(data);
            verify(refreshTokenReusePolicy).enforce(rawToken);
        }

        @Test
        @DisplayName("throws when token not found")
        void throwsWhenTokenNotFound() {
            String rawToken = "raw-token-value";

            when(refreshTokenRepository.find(rawToken)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.validate(rawToken, user)).isInstanceOf(InvalidRefreshTokenException.class);
        }

        @Test
        @DisplayName("throws when token version mismatch and revokes session")
        void throwsWhenTokenVersionMismatch() {
            String rawToken = "raw-token-value";
            RefreshTokenData data = new RefreshTokenData(user.getId(), 2, Instant.now(), "device-1");

            when(refreshTokenRepository.find(rawToken)).thenReturn(Optional.of(data));

            assertThatThrownBy(() -> refreshTokenService.validate(rawToken, user)).isInstanceOf(InvalidRefreshTokenException.class);
            verify(refreshTokenReusePolicy).enforce(rawToken);
            verify(refreshTokenRepository).revoke(rawToken);
            verify(refreshTokenRepository).removeFromUserSet(rawToken, user.getId());
        }

        @Test
        @DisplayName("throws and revokes all sessions when reuse detection is true")
        void throwsWhenReuseDetectionIsTrue() {
            String rawToken = "raw-token-value";

            doThrow((RefreshTokenReusedException.class)).when(refreshTokenReusePolicy).enforce(rawToken);

            assertThatThrownBy(() -> refreshTokenService.validate(rawToken, user)).isInstanceOf(RefreshTokenReusedException.class);
            verify(refreshTokenRepository, never()).find(rawToken);
        }
    }

    @Nested
    @DisplayName("revoke")
    class Revoke {

        @Test
        @DisplayName("revokes the token and removes it from the user's session set")
        void revokesTokenAndRemovesFromUserSet() {
            String rawToken = "raw-token-value";

            refreshTokenService.revoke(rawToken, user.getId());

            verify(refreshTokenRepository).revoke(rawToken);
            verify(refreshTokenRepository).removeFromUserSet(rawToken, user.getId());
        }
    }

    @Nested
    @DisplayName("revokeAllForUser")
    class RevokeAllForUser {

        @Test
        @DisplayName("revokes all sessions from the user's set")
        void revokesAllSessionsFromUserSet() {
            refreshTokenService.revokeAllForUser(user.getId());
            verify(refreshTokenRepository).revokeAllForUser(user.getId());
        }
    }

    @Nested
    @DisplayName("peek")
    class Peek {

        @Test
        @DisplayName("returns the token data when found and isn't reused")
        void returnsDataWhenTokenFoundAndIsNotReused() {
            String rawToken = "raw-token-value";
            RefreshTokenData data = new RefreshTokenData(user.getId(), user.getTokenVersion(), Instant.now(), "device-1");

            when(refreshTokenRepository.find(rawToken)).thenReturn(Optional.of(data));

            assertThat(refreshTokenService.peek(rawToken)).isEqualTo(data);
            verify(refreshTokenReusePolicy).enforce(rawToken);
        }

        @Test
        @DisplayName("throws when token isn't found")
        void throwsWhenTokenIsNotFound() {
            String rawToken = "raw-token-value";

            when(refreshTokenRepository.find(rawToken)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> refreshTokenService.peek(rawToken)).isInstanceOf(InvalidRefreshTokenException.class);
            verify(refreshTokenReusePolicy).enforce(rawToken);
        }
    }
}
