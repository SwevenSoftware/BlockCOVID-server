package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
  private final UserService userService;
  private final UserAuthenticationService authenticationService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  UserRegistrationService(
      UserService userService,
      UserAuthenticationService authenticationService,
      PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.authenticationService = authenticationService;
    this.passwordEncoder = passwordEncoder;
  }

  public Token register(Credentials credentials) throws IllegalArgumentException {
    userService
        .getByUsername(credentials.getUsername())
        .ifPresent(
            u -> {
              throw new IllegalArgumentException("Username already in use.");
            });
    UserBuilder builder = new UserBuilder();
    userService.save(
        builder
            .setUsername(credentials.getUsername())
            .setPassword(passwordEncoder.encode(credentials.getPassword()))
            .setAuthorities(credentials.getAuthorities())
            .createUser());
    return authenticationService.login(credentials.getUsername(), credentials.getPassword());
  }
}
