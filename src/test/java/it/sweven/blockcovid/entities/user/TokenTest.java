package it.sweven.blockcovid.entities.user;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class TokenTest {
  @Test
  void getAndSetId() {
    Token test = new Token("1", LocalDateTime.now().plusMinutes(5L), "user");
    test.setId("2");
    assertEquals("2", test.getId());
  }

  @Test
  void getAndSetUsername() {
    Token test = new Token("1", LocalDateTime.now().plusMinutes(5L), "user");
    test.setUsername("admin");
    assertEquals("admin", test.getUsername());
  }

  @Test
  void expiredAndGetExpiryDate() {
    LocalDateTime expected = LocalDateTime.now().minusMinutes(5L);
    Token test = new Token("1", expected, "user");
    assertEquals(expected, test.getExpiryDate());
    assertTrue(test.expired());
  }

  @Test
  void equalsWorksOnlyOnId() {
    Token first = new Token("1", LocalDateTime.now().plusMinutes(5L), "user");
    Token second = new Token("1", LocalDateTime.now().plusMinutes(10L), "admin");
    assertEquals(first, second);
  }

  @Test
  void equals() {
    Token first = new Token("1", LocalDateTime.now().plusMinutes(5L), "user");
    Token second = new Token("2", LocalDateTime.now().plusMinutes(10L), "admin");
    assertNotEquals(first, second);
  }

  @Test
  void toStringRightFormat() {
    Token test = new Token("1", LocalDateTime.now().plusMinutes(5L), "username");
    assertTrue(test.toString().contains("id="));
    assertTrue(test.toString().contains("expiryDate="));
    assertTrue(test.toString().contains("username="));
  }
}
