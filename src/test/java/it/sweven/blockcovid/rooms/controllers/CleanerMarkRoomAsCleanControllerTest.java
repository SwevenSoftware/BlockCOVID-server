package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CleanerMarkRoomAsCleanControllerTest {
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private CleanerMarkRoomAsCleanController router;

  @BeforeEach
  void setUp() {
    roomAssembler = mock(RoomAssembler.class);
    when(roomAssembler.setAuthorities(any())).thenReturn(roomAssembler);
    roomService = mock(RoomService.class);
    router = new CleanerMarkRoomAsCleanController(roomAssembler, roomService);
  }

  @Test
  void validMark() {
    User cleaner = mock(User.class);
    Room room = mock(Room.class);
    when(roomService.getByName("room")).thenReturn(room);
    when(roomService.save(any())).thenReturn(room);
    when(roomAssembler.toModel(any())).thenReturn(EntityModel.of(room));
    assertEquals(room, router.markAsClean(cleaner, "room").getContent());
  }

  @Test
  void invalidRoomName_throwsResponseStatusException() {
    when(roomService.setStatus("room", Status.CLEAN)).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.markAsClean(mock(User.class), "room"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
