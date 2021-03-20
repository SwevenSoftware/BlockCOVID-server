package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.Credentials;
import it.sweven.blockcovid.dto.TokenWithAuthorities;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserAuthenticationService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

class LoginRouterTest {

  private UserAuthenticationService service;
  private UserAssembler assembler;
  private LoginRouter router;

  @BeforeEach
  void setUp() {
    service = mock(UserAuthenticationService.class);
    assembler = mock(UserAssembler.class);
    when(assembler.setAuthorities(any())).thenAnswer(invocation -> assembler);
    when(assembler.toModel(any()))
        .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));
    router = new LoginRouter(service, assembler);
  }

  @Test
  void login_validCredentials() {
    Token expectedToken = mock(Token.class);
    User expectedUser = mock(User.class);
    when(service.login("user", "password")).thenReturn(expectedToken);
    when(service.authenticateByToken(any())).thenReturn(expectedUser);
    when(expectedUser.getAuthorities()).thenReturn(Set.of(Authority.USER));
    TokenWithAuthorities expected = new TokenWithAuthorities(expectedToken, Set.of(Authority.USER));
    assertEquals(expected, router.login(new Credentials("user", "password")).getContent());
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
