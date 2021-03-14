package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.entities.user.UserBuilder;
import javax.security.auth.login.CredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  UserRegistrationService(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  public User register(Credentials credentials) throws CredentialException {
    try {
      userService.getByUsername(credentials.getUsername());
      throw new CredentialException("Username already in use");
    } catch (UsernameNotFoundException e) {
      UserBuilder builder = new UserBuilder();
      User newUser =
          builder
              .setUsername(credentials.getUsername())
              .setPassword(passwordEncoder.encode(credentials.getPassword()))
              .setAuthorities(credentials.getAuthorities())
              .createUser();
      userService.save(newUser);
      return newUser;
    }
  }
}
