package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserRegistrationService;
import java.util.Set;
import javax.security.auth.login.CredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

class RegistrationControllerTest {
  private UserRegistrationService registrationService;
  private UserAssembler userAssembler;
  private RegistrationController controller;

  @BeforeEach
  void setUp() {
    registrationService = mock(UserRegistrationService.class);
    userAssembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(userAssembler.setAuthorities(anySet())).thenReturn(userAssembler);
    controller = new RegistrationController(registrationService, userAssembler);
  }

  @Test
  void register_validRequest() throws CredentialException {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
    User testUser = new User("user", "password", Set.of(Authority.USER));
    User testAdmin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(registrationService.register(any())).thenReturn(testUser);
    assertEquals(userAssembler.toModel(testUser), controller.register(testCredentials, testAdmin));
  }

  @Test
  void register_usernameAlreadyInUse_throwsResponseStatusException() throws CredentialException {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
    User adminTest = new User("admin", "password", Set.of(Authority.ADMIN));
    when(registrationService.register(any())).thenThrow(new CredentialException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.register(testCredentials, adminTest));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  void register_badCredentialsFromService_throwsResponseStatusException()
      throws CredentialException {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
    User adminTest = new User("admin", "password", Set.of(Authority.ADMIN));
    when(registrationService.register(any())).thenThrow(new BadCredentialsException(""));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.register(testCredentials, adminTest));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }
}
