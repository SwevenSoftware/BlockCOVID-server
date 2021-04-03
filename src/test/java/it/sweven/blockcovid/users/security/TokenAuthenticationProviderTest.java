package it.sweven.blockcovid.users.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class TokenAuthenticationProviderTest {

  private UserAuthenticationService service;
  private TokenAuthenticationProvider provider;

  @BeforeEach
  void setUp() {
    service = mock(UserAuthenticationService.class);
    provider = new TokenAuthenticationProvider(service);
  }

  @Test
  void additionalAuthenticationChecks_noExceptionThrown() {
    provider.additionalAuthenticationChecks(
        mock(User.class), mock(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void retrieveUser_validAuthentication() {
    UsernamePasswordAuthenticationToken authentication =
        mock(UsernamePasswordAuthenticationToken.class);
    when(authentication.getCredentials()).thenReturn("tokenId");
    User expectedUser = mock(User.class);
    when(service.authenticateByToken("tokenId")).thenReturn(expectedUser);
    assertEquals(expectedUser, provider.retrieveUser("user", authentication));
  }

  @Test
  void retrieveUser_invalidAuthentication_throwsBadCredentialsException() {
    UsernamePasswordAuthenticationToken authentication =
        mock(UsernamePasswordAuthenticationToken.class);
    when(authentication.getCredentials()).thenReturn("tokenId");
    when(service.authenticateByToken("tokenId")).thenReturn(null);
    assertThrows(
        BadCredentialsException.class, () -> provider.retrieveUser("user", authentication));
  }
}
