package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.entities.user.UserBuilder;
import it.sweven.blockcovid.security.Authority;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import it.sweven.blockcovid.services.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminRouterTest {

  private AdminRouter router;
  private UserAuthenticationService authenticationService;
  private UserRegistrationService registrationService;
  private UserAssembler userAssembler;

  @BeforeEach
  void setUp() {
    authenticationService = mock(UserAuthenticationService.class);
    registrationService = mock(UserRegistrationService.class);
    userAssembler = new UserAssembler();
    UserService userService = mock(UserService.class);
    router =
        new AdminRouter(authenticationService, registrationService, userAssembler, userService);
  }

  @Test
  void register() {
    Credentials testCredentials = new Credentials("test", "test", Set.of(Authority.USER));
    String auth = "testAuthorization";
    User testUser =
        new UserBuilder()
            .setUsername(testCredentials.getUsername())
            .setPassword(testCredentials.getPassword())
            .setAuthorities(Set.of(Authority.USER))
            .createUser();
    User testAdmin =
        new UserBuilder()
            .setUsername("testUser")
            .setPassword("testPassword")
            .setAuthorities(Set.of(Authority.ADMIN))
            .createUser();
    // assertEquals(userAssembler.toModel(testUser), router.register(testCredentials, auth));
  }
}
