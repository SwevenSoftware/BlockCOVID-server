package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public interface UserAuthenticationService {
  String login(String username, String password) throws BadCredentialsException;

  User authenticateByToken(String token) throws AuthenticationException;

  void logout(String username);
}
