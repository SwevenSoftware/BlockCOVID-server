package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
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
  public Token login(String username, String password) throws BadCredentialsException {
    System.out.println(tokenService.all());
    return userService
        .getByUsername(username)
        .filter(u -> passwordEncoder.matches(password, u.getPassword()))
        .map(
            u ->
                tokenService.save(
                    new Token(
                        UUID.randomUUID().toString(), LocalDateTime.now().plusDays(2), username)))
        .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));
  }

  @Override
  public User authenticateByToken(String token) throws AuthenticationException {
    return userService
        .getByUsername(tokenService.getToken(token).getUsername())
        .orElseThrow(() -> new BadCredentialsException("Token not found."));
  }

  @Override
  public void logout(String token) {
    tokenService.delete(token);
  }
}
