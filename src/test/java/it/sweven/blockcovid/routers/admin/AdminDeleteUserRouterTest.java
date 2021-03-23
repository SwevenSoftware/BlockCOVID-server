package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

class AdminDeleteUserRouterTest {
  private UserAssembler userAssembler;
  private UserService userService;
  private AdminDeleteUserRouter router;

  @BeforeEach
  void setUp() {}

  @Test
  void delete_validDeletion() {
    User userToDelete = new User("user", "password", Set.of(Authority.USER));
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(userService.deleteByUsername("user")).thenReturn(userToDelete);
    assertEquals(userToDelete, router.delete("user", admin).getContent());
  }

  @Test
  void delete_requestWithInvalidUsername_throwsResponseStatusException() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(userService.deleteByUsername("user"))
        .thenThrow(new UsernameNotFoundException("username user not found"));
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.delete("user", admin));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
