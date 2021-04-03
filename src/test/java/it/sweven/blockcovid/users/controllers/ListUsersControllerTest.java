package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doAnswer;

import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class ListUsersControllerTest {

  private UserService userService;
  private ListUsersController controller;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
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

    UserAssembler userAssembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(userAssembler.setAuthorities(anySet())).thenReturn(userAssembler);

    controller = new ListUsersController(userService, userAssembler);
  }

  @Test
  void listUsers_validRequest() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    List<User> expectedList =
        List.of(
            new User("user1", "password", Set.of(Authority.USER)),
            new User("user2", "password", Collections.emptySet()),
            new User("user3", "password", Set.of(Authority.CLEANER, Authority.ADMIN)));
    when(userService.getAllUsers()).thenReturn(expectedList);
    assertEquals(
        expectedList,
        controller.listUsers(admin).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }
}
