package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialChangeRequestForm;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.routers.user.UserModifyPasswordRouter;
import it.sweven.blockcovid.services.UserService;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.authentication.BadCredentialsException;

class UserModifyPasswordRouterTest {
  private UserAssembler assembler;
  private UserService userService;
  private UserModifyPasswordRouter userModifyPasswordRouter;

  @BeforeEach
  void setUp() {
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
              String oldPassword = invocation.getArgument(1);
              String newPassword = invocation.getArgument(2);
              if (!oldPassword.equals(user.getPassword()))
                throw new BadCredentialsException("Old password does not match");
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

    // Instantiation UserRoute
    userModifyPasswordRouter = new UserModifyPasswordRouter(assembler, userService);
  }

  @Test
  void modifyPassword_validCredentials() {
    User oldUser = new User("user", "password", Collections.emptySet());
    CredentialChangeRequestForm requestForm =
        new CredentialChangeRequestForm("password", "newPassword");
    User expectedUser = new User("user", "newPassword", Collections.emptySet());
    assertEquals(
        expectedUser, userModifyPasswordRouter.modifyPassword(oldUser, requestForm).getContent());
  }
}
