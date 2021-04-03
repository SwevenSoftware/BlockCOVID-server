package it.sweven.blockcovid.users.security;

import it.sweven.blockcovid.users.services.UserAuthenticationService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
  private final UserAuthenticationService userAuthenticationService;

  @Autowired
  TokenAuthenticationProvider(UserAuthenticationService userAuthenticationService) {
    this.userAuthenticationService = userAuthenticationService;
  }

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails,
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
      throws AuthenticationException {}

  @Override
  protected UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication) {
    Object token = authentication.getCredentials();
    return Optional.ofNullable(userAuthenticationService.authenticateByToken(String.valueOf(token)))
        .orElseThrow(() -> new BadCredentialsException("Invalid authentication token=" + token));
  }
}
