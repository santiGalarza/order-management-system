package com.santiGalarza.order_management.security.config;

import com.santiGalarza.order_management.security.JwtUtil;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.role.Permission;
import com.santiGalarza.order_management.user.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class JwtUtilTest {

    private static final String SECRET = "test-secret-key-must-be-long-enough-for-hmac-sha256";
    private static final long EXPIRATION = 3_600_000L;

    private JwtUtil jwtUtil;
    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);

        Permission orderRead = Permission.of("ORDER_READ",null);
        Permission orderCreate = Permission.of("ORDER_CREATE",null);

        Role role = Role.of("USER",null);
        role.getPermissions().add(orderRead);
        role.getPermissions().add(orderCreate);

        user = User.of("test@example.com","password", "Test","User");
        user.getRoles().add(role);
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("produces a non-null, non-empty token")
        void producesNonEmptyToken() {
            String token = jwtUtil.generateToken(user);

            assertThat(token).isNotEmpty();
            assertThat(token).isNotNull();
        }

        @Test
        @DisplayName("embeds the email as the subject")
        void embedsUsername() {
            String token = jwtUtil.generateToken(user);

            assertThat(jwtUtil.extractUsername(token)).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("embeds the permissions derived from the user's role")
        void embedsAuthorities() {
            String token = jwtUtil.generateToken(user);

            assertThat(jwtUtil.extractAuthorities(token)).containsExactly("ORDER_READ", "ORDER_CREATE");
        }
    }

    @Nested
    @DisplayName("extractUsername")
    class ExtractUsername {

        @Test
        @DisplayName("returns the email embedded at generation")
        void returnsEmbeddedSubject() {
            String token = jwtUtil.generateToken(user);

            assertThat(jwtUtil.extractUsername(token)).isEqualTo(user.getUsername());
        }
    }

    @Nested
    @DisplayName("isTokenValid")
    class IsTokenValid {

        @Test
        @DisplayName("returns true for a matching, unexpired token")
        void trueForMatchingUnexpiredToken() {
            String token = jwtUtil.generateToken(user);

            assertThat(jwtUtil.isTokenValid(token,user)).isTrue();
        }

        @Test
        @DisplayName("returns false when username does not match")
        void falseForUsernameMismatch() {
            String token = jwtUtil.generateToken(user);
            User otherUser = User.of("other@example.com", "password", "Other", "User");

            assertThat(jwtUtil.isTokenValid(token,otherUser)).isFalse();
        }

        @Test
        @DisplayName("returns false when token is expired")
        void falseForTokenExpired() {
            JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -10_000L);
            String token = expiredJwtUtil.generateToken(user);

            assertThat(expiredJwtUtil.isTokenValid(token,user)).isFalse();
        }
    }

    @Nested
    @DisplayName("isTokenExpired")
    class IsTokenExpired {

        @Test
        @DisplayName("returns false for a freshly generated token ")
        void falseForFreshlyGeneratedToken() {
            String token = jwtUtil.generateToken(user);

            assertThat(jwtUtil.isTokenExpired(token)).isFalse();
        }

        @Test
        @DisplayName("returns true once the expiration has passed")
        void trueOnceExpiredToken() {
            JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -10_000L);
            String token = expiredJwtUtil.generateToken(user);

            assertThat(expiredJwtUtil.isTokenExpired(token)).isTrue();
        }
    }

    @Nested
    @DisplayName("signature verification")
    class SignatureVerification {

        @Test
        @DisplayName("rejects a token signed with a different secret")
        void rejectsTokenFromDifferentSecret() {
            JwtUtil otherJwtUtil = new JwtUtil("a-completely-different-secret-key-for-signing", EXPIRATION);
            String token = otherJwtUtil.generateToken(user);

            assertThatThrownBy(() -> jwtUtil.extractUsername(token)).isInstanceOf(io.jsonwebtoken.security.SignatureException.class);
        }
    }
}
