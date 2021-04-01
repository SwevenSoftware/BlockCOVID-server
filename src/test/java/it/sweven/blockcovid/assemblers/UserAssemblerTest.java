package it.sweven.blockcovid.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserAssemblerTest {
  private UserAssembler assembler;

  @BeforeEach
  void init() {
    assembler = new UserAssembler();
  }

  @Test
  void toModel() {
    User fakeUser = mock(User.class);
    assertEquals(fakeUser, assembler.toModel(fakeUser).getContent());
  }

  @Test
  void adminToModel() {
    User fakeUser = mock(User.class);
    assertEquals(
        fakeUser, assembler.setAuthorities(Set.of(Authority.ADMIN)).toModel(fakeUser).getContent());
  }
}
