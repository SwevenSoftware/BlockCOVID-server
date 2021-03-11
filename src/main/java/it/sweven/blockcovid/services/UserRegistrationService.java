package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.security.Authority;
import java.time.LocalDateTime;
import java.util.Set;
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

  public Token register(String username, String password, Set<Authority> roles)
      throws IllegalArgumentException {
    userService
        .getByUsername(username)
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Username already in use.");
            });
    User user = new User(username, password, roles, LocalDateTime.now().plusMonths(3L));
    userService.save(user);
    return authenticationService.login(username, password);
  }
}
