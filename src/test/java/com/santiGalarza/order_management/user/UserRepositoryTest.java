package com.santiGalarza.order_management.user;

import com.santiGalarza.order_management.util.AbstractRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User persistUser(String email) {
        User user = User.of(email, "password", "First", "Last");
        return userRepository.save(user);
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("returns the corresponding user when provided its email")
        void returnsUserWhenProvidedEmail() {
            User user = persistUser("test@email.com");

            Optional<User> result = userRepository.findByEmail(user.getEmail());
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(user.getId());
            assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        }

        @Test
        @DisplayName("returns empty if email does not exist")
        void returnsEmptyWhenEmailDoesNotExist() {
            String email = "missing@email.com";

            Optional<User> result = userRepository.findByEmail(email);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("returns true if email exists")
        void returnsTrueIfEmailExists() {
            User user = persistUser("test@email.com");

            boolean result = userRepository.existsByEmail(user.getEmail());
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("returns false if email does not exist")
        void returnsFalseIfEmailDoesNotExist() {
            String email = "missing@email.com";

            boolean result = userRepository.existsByEmail(email);
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("populates createdAt and updatedAt on persist")
    void populatesAuditTimestampsOnPersist() {
        User user = persistUser("testEmail");

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
