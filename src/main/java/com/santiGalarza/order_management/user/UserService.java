package com.santiGalarza.order_management.user;

import com.santiGalarza.order_management.user.dto.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse getMe(String email){
        return userMapper.toResponseDto(findByEmail(email));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        return userMapper.toResponseDto(findById(id));
    }

    @Transactional
    public UserResponse updateMe(String email, UpdateUserRequest request) {
        User user = findByEmail(email);
        userMapper.updateUser(request, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse patchMe(String email, PatchUserRequest request) {
        User user = findByEmail(email);
        userMapper.patchUser(request, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        findById(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse assignRole(UUID id, AssignRoleRequest request) {
        User user = findById(id);
        user.getRoles().add(findRoleByName(request.getRoleName()));
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse revokeRole(UUID id, AssignRoleRequest request) {
        User user = findById(id);
        user.getRoles().remove(findRoleByName(request.getRoleName()));
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponse setActive(UUID id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    private User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException(name));
    }
}
