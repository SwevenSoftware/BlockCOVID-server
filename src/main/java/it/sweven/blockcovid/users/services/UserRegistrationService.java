package it.sweven.blockcovid.users.services;

import it.sweven.blockcovid.users.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.entities.UserBuilder;
import javax.security.auth.login.CredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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

  public User register(CredentialsWithAuthorities credentials)
      throws CredentialException, BadCredentialsException {
    if (credentials == null
        || credentials.getUsername() == null
        || credentials.getPassword() == null
        || credentials.getAuthorities() == null)
      throw new BadCredentialsException("Null field provided");
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
