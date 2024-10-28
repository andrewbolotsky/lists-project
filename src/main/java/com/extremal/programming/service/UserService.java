package com.extremal.programming.service;

import com.extremal.programming.entity.User;
import com.extremal.programming.model.UserDto;
import com.extremal.programming.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  public User createNewUser(String username, String password) {
    User user = new User();
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent() && !userOptional.get().getPassword().equals(password)) {
      throw new ResponseStatusException(HttpStatusCode.valueOf(400),
          "User with such username already exists and passwords differ");
    }
    user.setUsername(username);
    user.setPassword(password);
    return userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByUsername(username);
    if (userOptional.isEmpty()) {
      throw new UsernameNotFoundException(username);
    }
    UserDto userDto = new UserDto();
    userDto.setUsername(userOptional.get().getUsername());
    userDto.setPassword(userOptional.get().getPassword());
    return userDto;
  }
}
