package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class UUIDAuthenticationService implements UserAuthenticationService {
  private final UserService userService;

  @Autowired
  UUIDAuthenticationService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Token login(String username, String password) throws BadCredentialsException {
    return userService
        .getByUsername(username)
        .filter(u -> u.getPassword().equals(password))
        .map(
            u -> {
              u.setToken(new Token(UUID.randomUUID().toString(), LocalDateTime.now().plusDays(2)));
              userService.save(u);
              return u.getToken();
            })
        .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
  }

  @Override
  public User authenticateByToken(String token) throws AuthenticationException {
    return userService
        .getByToken(Token.fromString(token))
        .orElseThrow(() -> new BadCredentialsException("Token not found."));
  }

  @Override
  public void logout(String username) {
    userService
        .getByUsername(username)
        .ifPresent(
            u -> {
              u.setToken(null);
              userService.save(u);
            });
  }
}
