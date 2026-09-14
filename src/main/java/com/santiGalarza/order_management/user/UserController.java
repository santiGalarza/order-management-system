package com.santiGalarza.order_management.user;

import com.santiGalarza.order_management.security.config.RequiresPermission;

import com.santiGalarza.order_management.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @RequiresPermission.UserReadAll
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    @RequiresPermission.UserRead
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getMe(email));
    }

    @GetMapping
    @RequiresPermission.UserReadAll
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/me/change-password")
    @RequiresPermission.UserUpdate
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    @RequiresPermission.UserAssignRole
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID id,
            @Valid @RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(userService.assignRole(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresPermission.UserSetRole
    public ResponseEntity<UserResponse> setActive(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(userService.setActive(id, active));
    }

    @PutMapping("/me")
    @RequiresPermission.UserUpdate
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateMe(email, request));
    }

    @PatchMapping("/me")
    @RequiresPermission.UserUpdate
    public ResponseEntity<UserResponse> patchMe(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody PatchUserRequest request) {
        return ResponseEntity.ok(userService.patchMe(email, request));
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @RequiresPermission.UserAssignRole
    public ResponseEntity<Void> revokeRole(
            @PathVariable UUID id,
            @PathVariable String roleName) {
        userService.revokeRole(id, roleName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @RequiresPermission.UserDelete
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
