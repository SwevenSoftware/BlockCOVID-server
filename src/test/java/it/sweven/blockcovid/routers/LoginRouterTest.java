package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.dto.Credentials;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.services.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

class LoginRouterTest {

  private UserAuthenticationService service;
  private LoginRouter router;

  @BeforeEach
  void setUp() {
    service = mock(UserAuthenticationService.class);
    router = new LoginRouter(service);
  }

  @Test
  void login_validCredentials() {
    Token expectedToken = mock(Token.class);
    when(service.login("user", "password")).thenReturn(expectedToken);
    assertEquals(expectedToken, router.login(new Credentials("user", "password")).getContent());
  }

  @Test
  void login_invalidCredentials() {
    when(service.login("user", "password")).thenThrow(BadCredentialsException.class);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.login(new Credentials("user", "password")));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
