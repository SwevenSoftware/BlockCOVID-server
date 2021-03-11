package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public interface UserAuthenticationService {
  Token login(String username, String password) throws BadCredentialsException;

  User authenticateByToken(String token) throws AuthenticationException;

  void logout(String username);
}
