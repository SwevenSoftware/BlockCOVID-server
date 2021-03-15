package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.security.Authority;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import it.sweven.blockcovid.services.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.security.auth.login.CredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

class AdminRouterTest {

  private AdminRouter router;
  private UserAuthenticationService authenticationService;
  private UserRegistrationService registrationService;
  private UserAssembler userAssembler;
  private UserService userService;

  @BeforeEach
  void setUp() {
    // Mock AuthenticationService
    authenticationService = mock(UserAuthenticationService.class);
    // Basic mock AuthenticationService.authenticateByToken
    when(authenticationService.authenticateByToken(any())).thenReturn(new User());

    registrationService = mock(UserRegistrationService.class);

    // Mock UserAssembler
    userAssembler =
        spy(
            new UserAssembler() {
              @Override
              public EntityModel<User> toModel(User entity) {
                return EntityModel.of(entity);
              }
            });
    when(userAssembler.setAuthorities(anySet())).thenReturn(userAssembler);

    // Mock UserService
    userService = mock(UserService.class);
    // Mock UserService.updatePassword
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              String newPassword = invocation.getArgument(1);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .updatePassword(any(), any());
    // Mock UserService.updateAuthorities
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              Set<Authority> newAuthorities = invocation.getArgument(1);
              user.setAuthorities(newAuthorities);
              return null;
            })
        .when(userService)
        .updateAuthorities(any(), any());

    // Instantiation UserRoute
    router =
        new AdminRouter(authenticationService, registrationService, userAssembler, userService);
  }

  @Test
  void register_validRequest() throws CredentialException {
    Credentials testCredentials = new Credentials("user", "password", Set.of(Authority.USER));
    User testUser = new User("user", "password", Set.of(Authority.USER));
    User testAdmin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(testAdmin);
    when(registrationService.register(any())).thenReturn(testUser);
    assertEquals(userAssembler.toModel(testUser), router.register(testCredentials, "auth"));
  }

  @Test
  void register_wrongToken_throwsAuthenticationCredentialsException() {
    Credentials testCredentials = new Credentials("user", "password", Set.of(Authority.USER));
    when(authenticationService.authenticateByToken("auth"))
        .thenThrow(new AuthenticationCredentialsNotFoundException(""));
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> router.register(testCredentials, "auth"));
  }

  @Test
  void register_usernameAlreadyInUse_throwsResponseStatusException() throws CredentialException {
    Credentials testCredentials = new Credentials("user", "password", Set.of(Authority.USER));
    User adminTest = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(adminTest);
    when(registrationService.register(any())).thenThrow(new CredentialException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.register(testCredentials, "auth"));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  void register_nullCredentials_throwsResponseStatusException() throws CredentialException {
    User adminTest = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(adminTest);
    when(registrationService.register(any())).thenThrow(new NullPointerException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.register(null, "auth"));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void modifyUser_validRequest() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("user", "password", Collections.emptySet());
    Credentials newCredentials = new Credentials("newUser", "newPassword", Set.of(Authority.ADMIN));
    User expectedUser =
        new User(
            oldUser.getUsername(), newCredentials.getPassword(), newCredentials.getAuthorities());
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(userService.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
    assertEquals(
        expectedUser,
        router.modifyUser("auth", oldUser.getUsername(), newCredentials).getContent());
  }

  @Test
  void modifyUser_requestNotMadeByAdmin() {
    User user = new User("user", "password", Set.of(Authority.USER, Authority.CLEANER));
    User oldUser = new User("oldUser", "password", Collections.emptySet());
    Credentials newCredentials = new Credentials("newUser", "newPassword", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(user);
    when(userService.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.modifyUser("auth", oldUser.getUsername(), newCredentials));
    assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
  }

  @Test
  void modifyUser_nullCredentials() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("oldUser", "password", Collections.emptySet());
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(userService.getByUsername(oldUser.getUsername())).thenReturn(oldUser);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.modifyUser("auth", oldUser.getUsername(), null));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void modifyUser_usernameNotFound() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("oldUser", "password", Collections.emptySet());
    Credentials newCredentials = new Credentials("newUser", "newPassword", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(userService.getByUsername(oldUser.getUsername()))
        .thenThrow(new UsernameNotFoundException(oldUser.getUsername() + " not found"));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.modifyUser("auth", oldUser.getUsername(), newCredentials));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void listUsers_validRequest() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    List<User> expectedList =
        List.of(
            new User("user1", "password", Set.of(Authority.USER)),
            new User("user2", "password", Collections.emptySet()),
            new User("user3", "password", Set.of(Authority.CLEANER, Authority.ADMIN)));
    when(userService.getAllUsers()).thenReturn(expectedList);
    assertEquals(
        expectedList,
        router.listUsers("auth").getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }

  @Test
  void listUsers_requestNotMadeByAdmin() {
    User user = new User("user", "password", Set.of(Authority.USER, Authority.CLEANER));
    when(authenticationService.authenticateByToken("auth")).thenReturn(user);
    List<User> expectedList =
        List.of(
            new User("user1", "password", Set.of(Authority.USER)),
            new User("user2", "password", Collections.emptySet()),
            new User("user3", "password", Set.of(Authority.CLEANER, Authority.ADMIN)));
    when(userService.getAllUsers()).thenReturn(expectedList);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.listUsers("auth"));
    assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
  }
}
