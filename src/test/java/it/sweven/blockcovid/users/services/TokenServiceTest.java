package it.sweven.blockcovid.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.repositories.TokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

class TokenServiceTest {

  private TokenRepository repository;
  private TokenService service;

  @BeforeEach
  void setup() {
    repository = mock(TokenRepository.class);
    doAnswer(invocation -> Optional.of(mock(Token.class))).when(repository).deleteTokenById(any());
    service = new TokenService(repository);
  }

  @Test
  void getToken_validId() {
    Token expectedToken = new Token("idToken", LocalDateTime.of(2021, 10, 20, 9, 30), "user");
    when(repository.findById("idToken")).thenReturn(Optional.of(expectedToken));
    assertEquals(expectedToken, service.getToken("idToken"));
  }

  @Test
  void getToken_IdNotFound() {
    when(repository.findById("idToken")).thenReturn(Optional.empty());
    assertThrows(
        AuthenticationCredentialsNotFoundException.class, () -> service.getToken("idToken"));
  }

  @Test
  void save_checkReturnToken() {
    Token expectedToken = new Token("idToken", LocalDateTime.of(2021, 10, 20, 9, 30), "user");
    when(repository.save(expectedToken)).thenReturn(expectedToken);
    assertEquals(expectedToken, service.save(expectedToken));
  }

  @Test
  void save_checkRepositorySaveIsCalled() {
    Token expectedToken = new Token("idToken", LocalDateTime.of(2021, 10, 20, 9, 30), "user");
    AtomicBoolean tokenSaved = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              tokenSaved.set(true);
              return invocation.getArgument(0, Token.class);
            })
        .when(repository)
        .save(expectedToken);
    service.save(expectedToken);
    assertTrue(tokenSaved.get());
  }

  @Test
  void delete_checkRepositoryDeleteIsCalled() {
    AtomicBoolean tokenDeleted = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              tokenDeleted.set(true);
              return Optional.of(mock(Token.class));
            })
        .when(repository)
        .deleteTokenById(any());
    service.delete("idToken");
    assertTrue(tokenDeleted.get());
  }

  @Test
  void deleteEmptyOptional_throwsAuthenticationException() {
    when(repository.deleteTokenById(any())).thenReturn(Optional.empty());
    assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.delete("token"));
  }
}
