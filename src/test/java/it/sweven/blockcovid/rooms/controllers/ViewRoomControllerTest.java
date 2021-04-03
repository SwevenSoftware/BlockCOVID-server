package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ViewRoomControllerTest {
  private RoomService roomService;
  private DeskService deskService;
  private RoomWithDesksAssembler assembler;
  private ViewRoomController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    deskService = mock(DeskService.class);
    assembler = mock(RoomWithDesksAssembler.class);
    controller = new ViewRoomController(roomService, deskService, assembler);
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
    assertEquals(expectedEntityModel, controller.viewRoom(mock(User.class), "roomName"));
  }

  @Test
  void viewRoom_nonExistingRoom_throwsRoomNotFoundException() {
    when(roomService.getByName("roomName")).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.viewRoom(mock(User.class), "roomName"));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }
}
