package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
  private final UserService userService;
  private final UserAuthenticationService authenticationService;

  @Autowired
  UserRegistrationService(
      UserService userService, UserAuthenticationService authenticationService) {
    this.userService = userService;
    this.authenticationService = authenticationService;
  }

  public String register(String username, String password) throws IllegalArgumentException {
    userService
        .getByUsername(username)
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Username already in use.");
            });
    User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    userService.save(user);
    return authenticationService.login(username, password);
  }
}
