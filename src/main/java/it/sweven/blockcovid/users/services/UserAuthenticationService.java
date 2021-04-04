package it.sweven.blockcovid.users.services;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.entities.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public interface UserAuthenticationService {
  Token login(String username, String password) throws BadCredentialsException;

  User authenticateByToken(String token) throws AuthenticationException;

  void logout(String username);
}
