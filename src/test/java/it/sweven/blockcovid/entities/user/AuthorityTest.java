package it.sweven.blockcovid.entities.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthorityTest {
  @Test
  void getAuthorityReturnsEnumName() {
    Authority test = Authority.ADMIN;
    assertEquals(test.getAuthority(), test.name());
  }
}
