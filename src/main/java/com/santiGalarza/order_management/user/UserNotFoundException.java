package com.santiGalarza.order_management.user;

import com.santiGalarza.order_management.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {
  public UserNotFoundException(String email) {
    super("User not found with email: " + email);
  }

  public UserNotFoundException(UUID id) {
    super("User not found with id: " + id);
  }

}
