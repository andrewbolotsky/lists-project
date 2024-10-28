package com.extremal.programming.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.extremal.programming.entity.User;
import com.extremal.programming.model.UserDto;
import com.extremal.programming.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

public class UserServiceTest {

  private UserRepository userRepository;
  private UserService userService;

  @BeforeEach
  public void setUp() {
    userRepository = mock(UserRepository.class);
    userService = new UserService(userRepository);
  }

  @Test
  public void createNewUser_UserDoesNotExist_CreatesUser() {
    when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
    User userToSave = new User();
    userToSave.setUsername("newUser");
    userToSave.setPassword("password");
    when(userRepository.save(any(User.class))).thenReturn(userToSave);
    User createdUser = userService.createNewUser("newUser", "password");
    assertEquals("newUser", createdUser.getUsername());
    assertEquals("password", createdUser.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  public void createNewUser_UserExistsWithDifferentPassword_ThrowsException() {
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    existingUser.setPassword("differentPassword");
    when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> userService.createNewUser("existingUser", "password"));
    assertEquals("400 BAD_REQUEST \"User with such username already exists and passwords differ\"",
        exception.getMessage());
  }

  @Test
  public void loadUserByUsername_UserExists_ReturnsUserDto() {
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    existingUser.setPassword("password");
    when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));
    UserDto result = (UserDto) userService.loadUserByUsername("existingUser");
    assertEquals("existingUser", result.getUsername());
    assertEquals("password", result.getPassword());
  }

  @Test
  public void loadUserByUsername_UserDoesNotExist_ThrowsException() {
    when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("nonExistentUser"));
    assertEquals("nonExistentUser", exception.getMessage());
  }
}
