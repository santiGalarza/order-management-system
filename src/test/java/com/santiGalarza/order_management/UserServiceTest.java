package com.santiGalarza.order_management;

import com.santiGalarza.order_management.security.token.RefreshTokenService;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserMapper;
import com.santiGalarza.order_management.user.UserRepository;
import com.santiGalarza.order_management.user.UserService;
import com.santiGalarza.order_management.user.dto.*;
import com.santiGalarza.order_management.user.exception.UserNotFoundException;
import com.santiGalarza.order_management.user.role.Role;
import com.santiGalarza.order_management.user.role.RoleRepository;
import com.santiGalarza.order_management.user.role.exception.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.of("test@email.com", "password", "First Name", "Last Name");
        user.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("getMe")
    class GetMe {

        @Test
        @DisplayName("returns the mapped user when found")
        void returnsMappedUserWhenFound() {
            String email = "test@email.com";
            UserResponse response = new UserResponse();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.getMe(email);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            String email = "test@email.com";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getMe(email)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("returns all users mapped to response dtos")
        void returnsMappedUsers() {
            User secondUser = User.of("second@email.com", "password", "Second", "User");
            UserResponse firstResponse = new UserResponse();
            UserResponse secondResponse = new UserResponse();

            when(userRepository.findAll()).thenReturn(List.of(user, secondUser));
            when(userMapper.toResponseDto(user)).thenReturn(firstResponse);
            when(userMapper.toResponseDto(secondUser)).thenReturn(secondResponse);

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).containsExactly(firstResponse, secondResponse);
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("returns the mapped user when found")
        void returnsMappedUserWhenFound() {
            UUID id = user.getId();
            UserResponse response = new UserResponse();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.getUserById(id);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            UUID id = UUID.randomUUID();

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(id)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateMe")
    class UpdateMe {

        @Test
        @DisplayName("applies the update and returns the mapped result")
        void appliesUpdateAndReturnsMapped() {
            String email = "test@email.com";
            UpdateUserRequest request = mock(UpdateUserRequest.class);
            UserResponse response = new UserResponse();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.updateMe(email, request);

            verify(userMapper).updateUser(request, user);
            verify(userRepository).save(user);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            String email = "test@email.com";
            UpdateUserRequest request = mock(UpdateUserRequest.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateMe(email, request)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("patchMe")
    class PatchMe {

        @Test
        @DisplayName("applies the patch and returns the mapped result")
        void appliesPatchAndReturnsMapped() {
            String email = "test@email.com";
            PatchUserRequest request = mock(PatchUserRequest.class);
            UserResponse response = new UserResponse();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.patchMe(email, request);

            verify(userMapper).patchUser(request, user);
            verify(userRepository).save(user);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            String email = "test@email.com";
            PatchUserRequest request = mock(PatchUserRequest.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.patchMe(email, request)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("changePassword")
    class ChangePassword {

        @Test
        @DisplayName("updates password, increments token version, and revokes all sessions")
        void updatesPasswordAndRevokesSessions() {
            String email = "test@email.com";
            ChangePasswordRequest request = mock(ChangePasswordRequest.class);

            when(request.getCurrentPassword()).thenReturn("old-password");
            when(request.getNewPassword()).thenReturn("new-password");
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("old-password", user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

            int versionBefore = user.getTokenVersion();

            userService.changePassword(email, request);

            assertThat(user.getPassword()).isEqualTo("encoded-new-password");
            assertThat(user.getTokenVersion()).isEqualTo(versionBefore + 1);
            verify(userRepository).save(user);
            verify(refreshTokenService).revokeAllForUser(user.getId());
        }

        @Test
        @DisplayName("throws when current password does not match")
        void throwsWhenCurrentPasswordDoesNotMatch() {
            String email = "test@email.com";
            ChangePasswordRequest request = mock(ChangePasswordRequest.class);

            when(request.getCurrentPassword()).thenReturn("wrong-password");
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong-password", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword(email, request))
                    .isInstanceOf(BadCredentialsException.class);

            verify(userRepository, never()).save(any());
            verify(refreshTokenService, never()).revokeAllForUser(any());
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            String email = "test@email.com";
            ChangePasswordRequest request = mock(ChangePasswordRequest.class);

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.changePassword(email, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("deletes the user when found")
        void deletesUserWhenFound() {
            UUID id = user.getId();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            userService.deleteUser(id);

            verify(userRepository).deleteById(id);
        }

        @Test
        @DisplayName("throws and does not delete when user not found")
        void throwsWhenUserNotFound() {
            UUID id = UUID.randomUUID();

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(id)).isInstanceOf(UserNotFoundException.class);
            verify(userRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("assignRole")
    class AssignRole {

        @Test
        @DisplayName("adds the role and returns the mapped result")
        void addsRoleAndReturnsMapped() {
            UUID id = user.getId();
            Role role = Role.of("ADMIN", null);
            AssignRoleRequest request = mock(AssignRoleRequest.class);
            UserResponse response = new UserResponse();

            when(request.getRoleName()).thenReturn("ADMIN");
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.assignRole(id, request);

            assertThat(user.getRoles()).contains(role);
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            UUID id = UUID.randomUUID();
            AssignRoleRequest request = mock(AssignRoleRequest.class);

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.assignRole(id, request)).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("throws when role not found")
        void throwsWhenRoleNotFound() {
            UUID id = user.getId();
            AssignRoleRequest request = mock(AssignRoleRequest.class);

            when(request.getRoleName()).thenReturn("MISSING");
            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("MISSING")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.assignRole(id, request)).isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("revokeRole")
    class RevokeRole {

        @Test
        @DisplayName("removes the role from the user")
        void removesRoleFromUser() {
            UUID id = user.getId();
            Role role = Role.of("ADMIN", null);
            user.getRoles().add(role);

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));

            userService.revokeRole(id, "ADMIN");

            assertThat(user.getRoles()).doesNotContain(role);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            UUID id = UUID.randomUUID();

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.revokeRole(id, "ADMIN")).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("throws when role not found")
        void throwsWhenRoleNotFound() {
            UUID id = user.getId();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("MISSING")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.revokeRole(id, "MISSING")).isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("setActive")
    class SetActive {

        @Test
        @DisplayName("updates active status and returns the mapped result")
        void updatesActiveStatus() {
            UUID id = user.getId();
            UserResponse response = new UserResponse();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponseDto(user)).thenReturn(response);

            UserResponse result = userService.setActive(id, false);

            assertThat(user.isActive()).isFalse();
            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("throws when user not found")
        void throwsWhenUserNotFound() {
            UUID id = UUID.randomUUID();

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.setActive(id, true)).isInstanceOf(UserNotFoundException.class);
        }
    }
}