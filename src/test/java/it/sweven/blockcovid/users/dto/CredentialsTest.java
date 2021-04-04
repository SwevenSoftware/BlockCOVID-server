package it.sweven.blockcovid.users.dto;

import static org.junit.jupiter.api.Assertions.*;

import it.sweven.blockcovid.users.entities.Authority;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CredentialsTest {
  @Test
  void credentialsWithAuthorities() {
    Credentials testCredentials = new Credentials("user", "pass");
    CredentialsWithAuthorities derive =
        testCredentials.withAuthorities(Set.of(Authority.ADMIN, Authority.USER));
    assertEquals(testCredentials.getPassword(), derive.getPassword());
    assertEquals(testCredentials.getUsername(), derive.getUsername());
    assertEquals(derive.getAuthorities(), Set.of(Authority.ADMIN, Authority.USER));
  }
}
