package it.sweven.blockcovid.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

import it.sweven.blockcovid.services.UserAuthenticationService;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        Object token = authentication.getCredentials();
        return Optional
	    .ofNullable(token)
	    .flatMap(t ->
		     Optional.of(userAuthenticationService.authenticateByToken(String.valueOf(t)))
		     .map(u -> User.builder()
			  .username(u.getUsername())
			  .password(u.getPassword())
			  .roles("user")
			  .build()))
	    .orElseThrow(() -> new BadCredentialsException("Invalid authentication token=" + token));
    }
}
