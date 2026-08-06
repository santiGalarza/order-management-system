package com.santiGalarza.order_management.user.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)
    @NotBlank
    private String name;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static Permission of(String name, String description) {
        Permission permission = new Permission();
        permission.name = name;
        permission.description = description;
        return permission;
    }
}
