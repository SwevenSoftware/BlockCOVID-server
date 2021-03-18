package it.sweven.blockcovid.entities.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void newUser_unlockedEnabledAndCredentialsNotExpired() {
    User testUser = new User("user", "password", Collections.emptySet());
    assertTrue(testUser.isAccountNonLocked());
    assertTrue(testUser.isAccountNonExpired());
    assertTrue(testUser.isCredentialsNonExpired());
  }

  @Test
  void lockingAndUnlockingUser() {
    User testUser = new User("user", "pass", Collections.emptySet());
    testUser.lock();
    assertFalse(testUser.isAccountNonLocked());
    testUser.unlock();
    assertTrue(testUser.isAccountNonLocked());
  }

  @Test
  void enablingAndDisablingUser() {
    User testUser = new User("user", "pass", Collections.emptySet());
    testUser.disable();
    assertFalse(testUser.isEnabled());
    testUser.enable();
    assertTrue(testUser.isEnabled());
  }

  @Test
  void canTellIfCredentialsAreExpired() {
    User testUser =
        new User(
            "user",
            "pass",
            Collections.emptySet(),
            LocalDateTime.now().minusHours(2L),
            false,
            true);
    assertFalse(testUser.isCredentialsNonExpired());
  }

  @Test
  void credentialsCanBeChanged() {
    User testUser = new User();
    testUser.setPassword("pass");
    assertEquals("pass", testUser.getPassword());
    testUser.setUsername("user");
    assertEquals("user", testUser.getUsername());
    testUser.setAuthorities(Set.of(Authority.USER));
    assertEquals(Set.of(Authority.USER), testUser.getAuthorities());
  }

  @Test
  void equalsChecksTypes() {
    User testUser = new User();
    assertFalse(testUser.equals("Not a user"));
  }

  @Test
  void equalsWorks() {
    User testUser0 = new User("username", "password", Set.of(Authority.USER));
    User testUser1 = new User("username", "password", Set.of(Authority.USER));
    assertTrue(testUser0.equals(testUser1));
  }

  @Test
  void toStringRightFormat() {
    User testUser0 = new User("username", "password", Set.of(Authority.USER));
    assertTrue(testUser0.toString().contains("username="));
    assertTrue(testUser0.toString().contains("password="));
    assertTrue(testUser0.toString().contains("authorities="));
    assertTrue(testUser0.toString().contains("credentials_expDate="));
    assertTrue(testUser0.toString().contains("locked="));
    assertTrue(testUser0.toString().contains("enabled="));
  }
}
