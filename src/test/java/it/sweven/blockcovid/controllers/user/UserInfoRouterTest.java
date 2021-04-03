package it.sweven.blockcovid.controllers.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.User;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class UserInfoRouterTest {

  private UserInfoController router;
  private UserAssembler assembler;

  @BeforeEach
  void setUp() {
    assembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(assembler.setAuthorities(anySet())).thenReturn(assembler);
    router = new UserInfoController(assembler);
  }

  @Test
  void info_existingUser() {
    User user = new User("user", "password", Collections.emptySet());
    assertEquals(user, router.info(user).getContent());
  }
}
