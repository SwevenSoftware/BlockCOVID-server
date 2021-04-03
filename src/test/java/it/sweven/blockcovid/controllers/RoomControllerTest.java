package it.sweven.blockcovid.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.RoomWithDesks;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.DeskService;
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

class RoomControllerTest {

  private RoomService roomService;
  private DeskService deskService;
  private UserAuthenticationService authenticationService;
  private RoomWithDesksAssembler assembler;
  private RoomController router;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    deskService = mock(DeskService.class);
    authenticationService = mock(UserAuthenticationService.class);
    when(authenticationService.authenticateByToken("auth")).thenReturn(mock(User.class));
    assembler = mock(RoomWithDesksAssembler.class);
    router = new RoomController(roomService, deskService, authenticationService, assembler);
  }

  @Test
  void viewRoom_existingRoom() {
    Room expectedRoom = mock(Room.class);
    when(roomService.getByName("roomName")).thenReturn(expectedRoom);
    List<Desk> expectedDesks =
        List.of(new Desk(3, 45, "roomId"), new Desk(20, 11, "roomId"), new Desk(1, 10, "roomId"));
    when(deskService.getDesksByRoom("roomName")).thenReturn(expectedDesks);
    RoomWithDesks expectedRoomWithRoom =
        new RoomWithDesks(
            expectedRoom,
            expectedDesks.stream()
                .map(d -> new DeskInfo(d.getX(), d.getY()))
                .collect(Collectors.toList()));
    EntityModel<RoomWithDesks> expectedEntityModel = EntityModel.of(expectedRoomWithRoom);
    when(assembler.toModel(any())).thenReturn(expectedEntityModel);
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
    when(roomService.getAllRooms()).thenReturn(List.of(mock(Room.class), mock(Room.class)));
    List<RoomWithDesks> roomsWithDesks =
        List.of(mock(RoomWithDesks.class), mock(RoomWithDesks.class));
    CollectionModel<EntityModel<RoomWithDesks>> expectedCollection =
        CollectionModel.of(
            roomsWithDesks.stream().map(EntityModel::of).collect(Collectors.toList()));
    when(assembler.toCollectionModel(any())).thenReturn(expectedCollection);
    assertEquals(expectedCollection, router.listRooms("auth"));
  }
}
