package it.sweven.blockcovid.entities.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserTest {

  private User user;
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    passwordEncoder = mock(PasswordEncoder.class);
    user = new User();
    user.setPasswordEncoder(passwordEncoder);
  }

  @Test
  void checkPassword_CorrectPassword() {
    String rawPwd = "1234";
    user.setPassword(rawPwd);
    when(passwordEncoder.matches(rawPwd, user.getHashPassword())).thenReturn(true);
    assertTrue(user.checkPassword(rawPwd));
  }

  @Test
  void checkPassword_WrongRawPassword() {
    String rawPwd = "1234";
    user.setPassword(rawPwd);
    when(passwordEncoder.matches(rawPwd, user.getHashPassword())).thenReturn(true);
    assertFalse(user.checkPassword(rawPwd + "error"));
  }

  @Test
  void checkPassword_WrongHashPassword() {
    String rawPwd = "1234";
    user.setPassword(rawPwd);
    when(passwordEncoder.matches(rawPwd, user.getHashPassword() + "error")).thenReturn(true);
    assertFalse(user.checkPassword(rawPwd));
  }

  @Test
  void setPassword() {
    String rawPwd = "1234";
    passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    user.setPasswordEncoder(passwordEncoder);
    user.setPassword(rawPwd);
    assertEquals(user.getPassword(), rawPwd);
    assertTrue(passwordEncoder.matches(rawPwd, user.getHashPassword()));
  }
}
