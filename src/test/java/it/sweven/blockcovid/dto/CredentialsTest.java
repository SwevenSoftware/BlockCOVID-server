package it.sweven.blockcovid.dto;

import static org.junit.jupiter.api.Assertions.*;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CredentialsTest {
  @Test
  void credentialsWithAuthorities() {
    Credentials testCredentials = new Credentials("user", "pass");
    CredentialsWithAuthorities derivate =
        testCredentials.withAuthorities(Set.of(Authority.ADMIN, Authority.USER));
    assertEquals(testCredentials.getPassword(), derivate.getPassword());
    assertEquals(testCredentials.getUsername(), derivate.getUsername());
    assertEquals(derivate.getAuthorities(), Set.of(Authority.ADMIN, Authority.USER));
  }
}
