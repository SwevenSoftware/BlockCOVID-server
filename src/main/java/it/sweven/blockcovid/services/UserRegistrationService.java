package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
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

  public Token register(User user) throws IllegalArgumentException {
    userService
        .getByUsername(user.getUsername())
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Username already in use.");
            });
    userService.save(user);
    return authenticationService.login(user.getUsername(), user.getPassword());
  }
}
