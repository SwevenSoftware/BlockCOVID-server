package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserService;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class UserRouterTest {
  private UserAuthenticationService authenticationService;
  private UserAssembler assembler;
  private UserService userService;
  private UserRouter userRouter;

  @BeforeEach
  void setUp() {
    // Mock AuthenticationService
    authenticationService = mock(UserAuthenticationService.class);
    // Basic mock AuthenticationService.authenticateByToken
    when(authenticationService.authenticateByToken(any())).thenReturn(new User());

    // Mock UserAssembler
    assembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(assembler.setAuthorities(anySet())).thenReturn(assembler);

    // Mock UserService
    userService = mock(UserService.class);
    // Mock UserService.updatePassword
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              String newPassword = invocation.getArgument(1);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .updatePassword(any(), any());
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

    // Instantiation UserRoute
    userRouter = new UserRouter(authenticationService, assembler, userService);
  }

  @Test
  void info_existingUser() {
    User user = new User("user", "password", Collections.emptySet());
    when(authenticationService.authenticateByToken("auth")).thenReturn(user);
    assertEquals(user, userRouter.info("auth").getContent());
  }

  @Test
  void modifyPassword_validCredentials() {
    User oldUser = new User("user", "password", Collections.emptySet());
    Credentials newCredentials = new Credentials("newUser", "newPassword", Set.of(Authority.ADMIN));
    User expectedUser = new User("user", "newPassword", Collections.emptySet());
    when(authenticationService.authenticateByToken("auth")).thenReturn(oldUser);
    assertEquals(expectedUser, userRouter.modifyPassword("auth", newCredentials).getContent());
  }

  @Test
  void modifyPassword_nullCredentials() {
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> userRouter.modifyPassword("", null));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void modifyPassword_nullPasswordCredentials() {
    Credentials newCredentials = new Credentials("newUsername", null, Set.of(Authority.ADMIN));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> userRouter.modifyPassword("", newCredentials));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
