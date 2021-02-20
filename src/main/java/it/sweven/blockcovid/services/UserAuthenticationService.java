package it.sweven.blockcovid.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import it.sweven.blockcovid.entities.User;

public interface UserAuthenticationService {
    String login(String username, String password) throws BadCredentialsException;
    User authenticateByToken(String token) throws AuthenticationException;
    void logout(String username);
}
