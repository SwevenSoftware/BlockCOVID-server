package it.sweven.blockcovid.entities.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorityTest {
    @Test
    void getAuthorityReturnsEnumName() {
        Authority test = Authority.ADMIN;
        assertEquals(test.getAuthority(), test.name());
    }
}