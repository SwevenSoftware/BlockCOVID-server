package it.sweven.blockcovid.users.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.entities.User;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class AccountInfoRouterTest {

  private AccountInfoController controller;

  @BeforeEach
  void setUp() {
    UserAssembler assembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(assembler.setAuthorities(anySet())).thenReturn(assembler);
    controller = new AccountInfoController(assembler);
  }

  @Test
  void info_existingUser() {
    User user = new User("user", "password", Collections.emptySet());
    assertEquals(user, controller.info(user).getContent());
  }
}
