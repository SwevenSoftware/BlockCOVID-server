package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.RoomService;
import it.sweven.blockcovid.services.UserAuthenticationService;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class RoomRouterTest {

  private RoomService roomService;
  private UserAuthenticationService authenticationService;
  private RoomAssembler assembler;
  private RoomRouter router;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    authenticationService = mock(UserAuthenticationService.class);
    when(authenticationService.authenticateByToken("auth")).thenReturn(mock(User.class));
    assembler = mock(RoomAssembler.class);
    router = new RoomRouter(roomService, authenticationService, assembler);
  }

  @Test
  void viewRoom_existingRoom() {
    Room expectedRoom = mock(Room.class);
    EntityModel<Room> expectedEntityModel = EntityModel.of(expectedRoom);
    when(roomService.getByName("roomName")).thenReturn(expectedRoom);
    when(assembler.toModel(expectedRoom)).thenReturn(expectedEntityModel);
    assertEquals(expectedEntityModel, router.viewRoom("roomName", "auth"));
  }

  @Test
  void viewRoom_nonExistingRoom_throwsRoomNotFoundException() {
    when(roomService.getByName("roomName")).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.viewRoom("roomName", "auth"));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }

  @Test
  void listRooms() {
    List<Room> rooms = List.of(mock(Room.class), mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(rooms);
    CollectionModel<EntityModel<Room>> expectedCollection =
        CollectionModel.of(rooms.stream().map(EntityModel::of).collect(Collectors.toList()));
    when(assembler.toCollectionModel(rooms)).thenReturn(expectedCollection);
    assertEquals(expectedCollection, router.listRooms("auth"));
  }
}
