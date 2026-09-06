package com.santiGalarza.order_management.security.config;

import com.santiGalarza.order_management.security.auth.dto.LoginRequest;
import com.santiGalarza.order_management.user.User;
import com.santiGalarza.order_management.user.UserRepository;
import com.santiGalarza.order_management.user.role.Role;
import com.santiGalarza.order_management.user.role.RoleRepository;
import com.santiGalarza.order_management.util.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import tools.jackson.databind.json.JsonMapper;

public class AuthControllerLoginTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User persistUser(String email, String rawPassword) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Seed role USER not found"));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName("First")
                .lastName("Last")
                .roles(Set.of(userRole))
                .build();

        return userRepository.save(user);
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("returns access and refresh tokens for valid credentials")
        void returnsTokensForValidCredentials() throws Exception {
            persistUser("test@email.com", "password123");

            LoginRequest request = new LoginRequest();
            request.setEmail("test@email.com");
            request.setPassword("password123");

            mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty());
        }

        @Test
        @DisplayName("returns 401 for incorrect password")
        void returnsUnauthorizedForIncorrectPassword() throws Exception {
            persistUser("test@email.com", "password123");

            LoginRequest request = new LoginRequest();
            request.setEmail("test@email.com");
            request.setPassword("wrongPassword");

            mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 401 for a non-existent email")
        void returnsUnauthorizedForNonExistentEmail() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("doesnotexist@email.com");
            request.setPassword("password123");

            mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }
}