package it.sweven.blockcovid.users.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.TokenService;
import it.sweven.blockcovid.users.services.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class TokenAuthenticationProviderTest {

  private UserAuthenticationService service;
  private TokenAuthenticationProvider provider;
  private TokenService tokenService;
  private Token fakeToken;
  private UsernamePasswordAuthenticationToken fakeUsernameAuthToken;

  @BeforeEach
  void setUp() {
    service = mock(UserAuthenticationService.class);
    tokenService = mock(TokenService.class);
    fakeToken = mock(Token.class);
    when(fakeToken.expired()).thenReturn(false);
    when(tokenService.getToken(any())).thenReturn(fakeToken);
    fakeUsernameAuthToken = mock(UsernamePasswordAuthenticationToken.class);
    when(fakeUsernameAuthToken.getCredentials()).thenReturn("token");
    provider = new TokenAuthenticationProvider(service, tokenService);
  }

  @Test
  void additionalAuthenticationChecks_noExceptionThrown() {
    provider.additionalAuthenticationChecks(mock(User.class), fakeUsernameAuthToken);
  }

  @Test
  void additionalAuthenticationChecksTokenExpiredAuthenticationException() {
    when(fakeToken.expired()).thenReturn(true);
    assertThrows(
        CredentialsExpiredException.class,
        () -> provider.additionalAuthenticationChecks(mock(User.class), fakeUsernameAuthToken));
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
