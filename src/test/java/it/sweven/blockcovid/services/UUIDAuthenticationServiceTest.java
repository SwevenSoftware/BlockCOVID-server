package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

class UUIDAuthenticationServiceTest {

  private UserService userService;
  private TokenService tokenService;
  private PasswordEncoder encoder;
  private UUIDAuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    tokenService = mock(TokenService.class);
    encoder = mock(PasswordEncoder.class);
    when(encoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    authenticationService = new UUIDAuthenticationService(userService, encoder, tokenService);
  }

  @Test
  void login_validCredentials_checkReturnToken() {
    User user = new User("user", "password", Set.of(Authority.USER));
    when(userService.getByUsername("user")).thenReturn(user);
    when(encoder.matches("password", user.getPassword())).thenReturn(true);
    when(tokenService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    assertEquals("user", authenticationService.login("user", "password").getUsername());
  }

  @Test
  void login_validCredentials_checkTokenServiceSaveIsCalled() {
    User user = new User("user", "password", Collections.emptySet());
    when(userService.getByUsername("user")).thenReturn(user);
    when(encoder.matches("password", user.getPassword())).thenReturn(true);
    AtomicBoolean tokenSaved = new AtomicBoolean(false);
    when(tokenService.save(any()))
        .thenAnswer(
            invocation -> {
              tokenSaved.set(true);
              return invocation.getArgument(0);
            });
    authenticationService.login("user", "password");
    assertTrue(tokenSaved.get());
  }

  @Test
  void authenticateByToken_validTokenId() {
    User expectedUser = new User("user", "password", Set.of(Authority.ADMIN));
    Token token = mock(Token.class);
    when(token.getUsername()).thenReturn("user");
    when(tokenService.getToken("tokenId")).thenReturn(token);
    when(userService.getByUsername("user")).thenReturn(expectedUser);
    assertEquals(expectedUser, authenticationService.authenticateByToken("tokenId"));
  }

  @Test
  void authenticateByToken_invalidTokenId_throwsAuthenticationCredentialsNotFoundException() {
    when(tokenService.getToken("tokenId"))
        .thenThrow(AuthenticationCredentialsNotFoundException.class);
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> authenticationService.authenticateByToken("tokenId"));
  }

  @Test
  void authenticateByToken_invalidUsername_throwsUsernameNotFoundException() {
    Token token = mock(Token.class);
    when(token.getUsername()).thenReturn("user");
    when(tokenService.getToken("tokenId")).thenReturn(token);
    when(userService.getByUsername("user")).thenThrow(UsernameNotFoundException.class);
    assertThrows(
        UsernameNotFoundException.class,
        () -> authenticationService.authenticateByToken("tokenId"));
  }

  @Test
  void logout_checkTokenServiceDeleteIsCalled() {
    AtomicBoolean tokenDeleted = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              tokenDeleted.set(true);
              return null;
            })
        .when(tokenService)
        .delete(anyString());
    authenticationService.logout("tokenId");
    assertTrue(tokenDeleted.get());
  }
}
