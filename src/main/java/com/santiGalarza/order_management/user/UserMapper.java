package com.santiGalarza.order_management.user;

import com.santiGalarza.order_management.user.dto.PatchUserRequest;
import com.santiGalarza.order_management.user.dto.UpdateUserRequest;
import com.santiGalarza.order_management.user.dto.UserResponse;
import com.santiGalarza.order_management.user.role.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = Role.class)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "permissions", expression = "java(user.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toSet()))")
    UserResponse toResponseDto(User user);

    void updateUser(UpdateUserRequest request, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUser(PatchUserRequest request, @MappingTarget User user);
}
