package com.santiGalarza.order_management.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "permissions", expression = "java(user.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toSet()))")
    UserResponse toResponseDto(User user);
}
