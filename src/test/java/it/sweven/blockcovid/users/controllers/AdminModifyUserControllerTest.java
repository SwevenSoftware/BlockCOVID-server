package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserService;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

class AdminModifyUserControllerTest {

  private UserAssembler userAssembler;
  private UserService userService;
  private AdminModifyUserController router;

  @BeforeEach
  void setUp() {
    userAssembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(userAssembler.setAuthorities(anySet())).thenReturn(userAssembler);

    userService = mock(UserService.class);
    // Mock UserService.updatePassword
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              String newPassword = invocation.getArgument(2);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .updatePassword(any(), any(), any());
    // Mock UserService.updateAuthorities
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              Set<Authority> newAuthorities = invocation.getArgument(1);
              user.setAuthorities(newAuthorities);
              return null;
            })
        .when(userService)
        .updateAuthorities(any(), any());
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              String newPassword = invocation.getArgument(1);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .setPassword(any(), any());

    router = new AdminModifyUserController(userAssembler, userService);
  }

  @Test
  void modifyUser_validRequest() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("user", "password", Collections.emptySet());
    CredentialsWithAuthorities newCredentials =
        new CredentialsWithAuthorities("newUser", "newPassword", Set.of(Authority.ADMIN));
    User expectedUser =
        new User(
            oldUser.getUsername(), newCredentials.getPassword(), newCredentials.getAuthorities());
    when(userService.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
    assertEquals(
        expectedUser, router.modifyUser(admin, oldUser.getUsername(), newCredentials).getContent());
  }

  @Test
  void modifyUser_usernameNotFound() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("oldUser", "password", Collections.emptySet());
    CredentialsWithAuthorities newCredentials =
        new CredentialsWithAuthorities("newUser", "newPassword", Set.of(Authority.ADMIN));
    when(userService.getByUsername(oldUser.getUsername()))
        .thenThrow(new UsernameNotFoundException(oldUser.getUsername() + " not found"));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.modifyUser(admin, oldUser.getUsername(), newCredentials));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
