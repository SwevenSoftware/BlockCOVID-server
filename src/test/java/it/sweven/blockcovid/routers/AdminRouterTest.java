package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.DeskAssembler;
import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.DeskWithRoomName;
import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.BadAttributeValueExpException;
import javax.security.auth.login.CredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
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
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private DeskService deskService;
  private DeskAssembler deskAssembler;

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
              String newPassword = invocation.getArgument(2);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .updatePassword(any(), any(), any());
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
    doAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              String newPassword = invocation.getArgument(1);
              user.setPassword(newPassword);
              return null;
            })
        .when(userService)
        .setPassword(any(), any());

    roomAssembler = mock(RoomAssembler.class);
    roomService = mock(RoomService.class);

    deskService = mock(DeskService.class);
    deskAssembler = mock(DeskAssembler.class);
    when(deskAssembler.setAuthorities(any())).thenAnswer(invocation -> deskAssembler);

    // Instantiation UserRoute
    router =
        new AdminRouter(
            authenticationService,
            registrationService,
            userAssembler,
            userService,
            roomAssembler,
            roomService,
            deskService,
            deskAssembler);
  }

  @Test
  void register_validRequest() throws CredentialException {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
    User testUser = new User("user", "password", Set.of(Authority.USER));
    User testAdmin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(testAdmin);
    when(registrationService.register(any())).thenReturn(testUser);
    assertEquals(userAssembler.toModel(testUser), router.register(testCredentials, "auth"));
  }

  @Test
  void register_wrongToken_throwsAuthenticationCredentialsException() {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
    when(authenticationService.authenticateByToken("auth"))
        .thenThrow(new AuthenticationCredentialsNotFoundException(""));
    assertThrows(
        AuthenticationCredentialsNotFoundException.class,
        () -> router.register(testCredentials, "auth"));
  }

  @Test
  void register_usernameAlreadyInUse_throwsResponseStatusException() throws CredentialException {
    CredentialsWithAuthorities testCredentials =
        new CredentialsWithAuthorities("user", "password", Set.of(Authority.USER));
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
  void register_requestNotMadeByAdmin() {
    User user = mock(User.class);
    when(user.getAuthorities()).thenReturn(Set.of(Authority.USER, Authority.CLEANER));
    when(authenticationService.authenticateByToken("auth")).thenReturn(user);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.register(mock(CredentialsWithAuthorities.class), "auth"));
    assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
  }

  @Test
  void modifyUser_validRequest() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    User oldUser = new User("user", "password", Collections.emptySet());
    CredentialsWithAuthorities newCredentials =
        new CredentialsWithAuthorities("newUser", "newPassword", Set.of(Authority.ADMIN));
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
    CredentialsWithAuthorities newCredentials =
        new CredentialsWithAuthorities("newUser", "newPassword", Set.of(Authority.ADMIN));
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
    CredentialsWithAuthorities newCredentials =
        new CredentialsWithAuthorities("newUser", "newPassword", Set.of(Authority.ADMIN));
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

  @Test
  void delete_validDeletion() {
    User userToDelete = new User("user", "password", Set.of(Authority.USER));
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(userService.deleteByUsername("user")).thenReturn(userToDelete);
    assertEquals(userToDelete, router.delete("user", "auth").getContent());
  }

  @Test
  void delete_requestNotMadeByAdmin_throwsResponseStatusException() {
    User submitter = new User("user", "password", Set.of(Authority.USER, Authority.CLEANER));
    when(authenticationService.authenticateByToken("auth")).thenReturn(submitter);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.delete("user", "auth"));
    assertEquals(HttpStatus.FORBIDDEN, thrown.getStatus());
  }

  @Test
  void delete_requestWithInvalidUsername_throwsResponseStatusException() {
    User admin = new User("admin", "password", Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(userService.deleteByUsername("user"))
        .thenThrow(new UsernameNotFoundException("username user not found"));
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.delete("user", "auth"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void newRoom_requestWithValidRoomInfo() throws BadAttributeValueExpException {
    RoomInfo roomInfo =
        new RoomInfo(
            "testRoom", LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);

    Room fakeRoom = mock(Room.class);
    EntityModel fakeModel = mock(EntityModel.class);
    when(authenticationService.authenticateByToken("auth"))
        .thenReturn(new User("", "", Set.of(Authority.ADMIN)));
    when(roomService.createRoom(any())).thenReturn(fakeRoom);
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    assertEquals(fakeModel, router.newRoom("auth", roomInfo));
  }

  @Test
  void newRoom_requestWithInvalidRoomInfo() throws BadAttributeValueExpException {
    RoomInfo roomInfo =
        new RoomInfo(
            null, LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);

    Room fakeRoom = mock(Room.class);
    EntityModel fakeModel = mock(EntityModel.class);
    when(authenticationService.authenticateByToken("auth"))
        .thenReturn(new User("", "", Set.of(Authority.ADMIN)));
    when(roomService.createRoom(any())).thenThrow(new BadAttributeValueExpException(null));
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.newRoom("auth", roomInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void newRoom_requestNotMadeByAdmin() {
    User fakeUser = mock(User.class);
    RoomInfo fakeInfo = mock(RoomInfo.class);
    when(fakeUser.getAuthorities()).thenReturn(Set.of(Authority.USER));
    when(authenticationService.authenticateByToken("auth")).thenReturn(fakeUser);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.newRoom("auth", fakeInfo));
    assertEquals(thrown.getStatus(), HttpStatus.FORBIDDEN);
  }

  @Test
  void addDesk_validRequest() throws DeskNotAvailable {
    User admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    Set<DeskInfo> providedDesks =
        Set.of(new DeskInfo(1234, 5, 10), new DeskInfo(3, 11, 40), new DeskInfo(22, 1, 1));
    for (DeskInfo desk : providedDesks) {
      when(deskService.addDesk(desk, "roomName")).thenReturn(mock(Desk.class));
    }
    List<DeskWithRoomName> expectedList =
        List.of(
            new DeskWithRoomName(1234, "roomName", 5, 10),
            new DeskWithRoomName(3, "roomName", 11, 40),
            new DeskWithRoomName(22, "roomName", 1, 1));
    when(deskAssembler.toCollectionModel(any()))
        .thenAnswer(
            invocation ->
                CollectionModel.of(
                    expectedList.stream().map(EntityModel::of).collect(Collectors.toList())));
    assertEquals(
        expectedList,
        router.addDesk("roomName", "auth", providedDesks).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }

  @Test
  void addDesk_requestNotMadeByAdmin() {
    User authUser = mock(User.class);
    when(authUser.getAuthorities()).thenReturn(Set.of(Authority.USER, Authority.CLEANER));
    when(authenticationService.authenticateByToken("auth")).thenReturn(authUser);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.addDesk("", "auth", Set.of(mock(DeskInfo.class), mock(DeskInfo.class))));
    assertEquals(thrown.getStatus(), HttpStatus.FORBIDDEN);
  }

  @Test
  void addDesk_deskIsNotAvailable() throws DeskNotAvailable {
    User admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    DeskInfo faultyDesk = mock(DeskInfo.class);
    Set<DeskInfo> providedDesks = Set.of(mock(DeskInfo.class), faultyDesk, mock(DeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new DeskNotAvailable());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.addDesk("roomName", "auth", providedDesks));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  void addDesk_providedRoomNotFound() throws DeskNotAvailable {
    User admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(deskService.addDesk(any(), eq("roomName"))).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                router.addDesk(
                    "roomName", "auth", Set.of(mock(DeskInfo.class), mock(DeskInfo.class))));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }

  @Test
  void addDesk_IllegalArgumentProvided() throws DeskNotAvailable {
    User admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    when(authenticationService.authenticateByToken("auth")).thenReturn(admin);
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    DeskInfo faultyDesk = mock(DeskInfo.class);
    Set<DeskInfo> providedDesks = Set.of(mock(DeskInfo.class), faultyDesk, mock(DeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new IllegalArgumentException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.addDesk("roomName", "auth", providedDesks));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }
}
