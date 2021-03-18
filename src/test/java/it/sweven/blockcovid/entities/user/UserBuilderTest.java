package it.sweven.blockcovid.entities.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class UserBuilderTest {
  @Test
  void rightCreateUser() {
    UserBuilder builder = new UserBuilder();
    User expected = new User("user", "password", Set.of(Authority.USER));
    User generated =
        builder
            .setUsername("user")
            .setPassword("password")
            .setAuthorities(Set.of(Authority.USER))
            .createUser();
    assertEquals(expected, generated);
  }

  @Test
  void createUserWithoutAuthoritiesSetsThemAsEmptySet() {
    UserBuilder builder = new UserBuilder();
    User generated = builder.setUsername("user").setPassword("password").createUser();
    assertEquals(generated.getAuthorities(), Collections.emptySet());
  }

  @Test
  void noUsername_throwsBadCredentialsException() {
    UserBuilder builder = new UserBuilder();
    assertThrows(BadCredentialsException.class, () -> builder.setPassword("password").createUser());
  }

  @Test
  void noPassword_throwsBadCredentialsException() {
    UserBuilder builder = new UserBuilder();
    assertThrows(BadCredentialsException.class, () -> builder.setUsername("user").createUser());
  }
}
