package it.sweven.blockcovid.users.services;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UUIDAuthenticationService implements UserAuthenticationService {
  private final UserService userService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  UUIDAuthenticationService(
      UserService userService, PasswordEncoder passwordEncoder, TokenService tokenService) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
  }

  @Override
  public Token login(String username, String password) throws AuthenticationException {
    User user = userService.getByUsername(username);
    if (passwordEncoder.matches(password, user.getPassword()))
      return tokenService.save(
          new Token(UUID.randomUUID().toString(), LocalDateTime.now().plusDays(2), username));
    else throw new BadCredentialsException("Invalid username or password.");
  }

  @Override
  public User authenticateByToken(String token) throws AuthenticationException {
    return userService.getByUsername(tokenService.getToken(token).getUsername());
  }

  @Override
  public void logout(String token) {
    tokenService.delete(token);
  }
}
