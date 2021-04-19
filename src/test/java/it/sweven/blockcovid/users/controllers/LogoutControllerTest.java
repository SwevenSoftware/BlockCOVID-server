package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.server.ResponseStatusException;

class LogoutControllerTest {

  private TokenService tokenService;
  private LogoutController logoutController;

  @BeforeEach
  void setUp() {
    tokenService = mock(TokenService.class);
    logoutController = new LogoutController(tokenService);
  }

  @Test
  void happyPath() {
    Token fakeToken = mock(Token.class);
    when(tokenService.delete(anyString())).thenReturn(fakeToken);
    assertEquals(fakeToken, logoutController.logout("token").getContent());
  }

  @Test
  void authenticationException_throwsResponseStatusException() {
    when(tokenService.delete(anyString()))
        .thenThrow(new AuthenticationCredentialsNotFoundException(""));

    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> logoutController.logout("token"));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
