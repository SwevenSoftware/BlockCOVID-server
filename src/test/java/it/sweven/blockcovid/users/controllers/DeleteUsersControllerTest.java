package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

class DeleteUsersControllerTest {
  private UserService userService;
  private DeleteUserController controller;

  @BeforeEach
  void setUp() {
    UserAssembler userAssembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(userAssembler.setAuthorities(anySet())).thenReturn(userAssembler);

    userService = mock(UserService.class);
    controller = new DeleteUserController(userAssembler, userService);
  }

  @Test
  void delete_validDeletion() {
    User userToDelete = new User("user", "password", Set.of(Authority.USER));
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(userService.deleteByUsername("user")).thenReturn(userToDelete);
    assertEquals(userToDelete, controller.delete("user", admin).getContent());
  }

  @Test
  void delete_requestWithInvalidUsername_throwsResponseStatusException() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(userService.deleteByUsername("user"))
        .thenThrow(new UsernameNotFoundException("username user not found"));
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.delete("user", admin));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
