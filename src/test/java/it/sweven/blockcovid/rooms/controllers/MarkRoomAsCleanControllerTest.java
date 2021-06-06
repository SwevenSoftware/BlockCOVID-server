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

class MarkRoomAsCleanControllerTest {
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private MarkRoomAsCleanController controller;

  @BeforeEach
  void setUp() {
    roomAssembler = mock(RoomAssembler.class);
    when(roomAssembler.setAuthorities(any())).thenReturn(roomAssembler);
    roomService = mock(RoomService.class);
    controller = new MarkRoomAsCleanController(roomAssembler, roomService);
  }

  @Test
  void validMark() {
    User cleaner = mock(User.class);
    when(cleaner.getUsername()).thenReturn("cleaner");
    Room room = mock(Room.class);
    when(roomService.setStatus("room", Status.CLEAN, "cleaner")).thenReturn(room);
    when(roomAssembler.toModel(room)).thenReturn(EntityModel.of(room));
    assertEquals(room, controller.markAsClean(cleaner, "room").getContent());
  }

  @Test
  void invalidRoomName_throwsResponseStatusException() {
    User cleaner = mock(User.class);
    when(cleaner.getUsername()).thenReturn("cleaner");
    when(roomService.setStatus("room", Status.CLEAN, "cleaner"))
        .thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.markAsClean(cleaner, "room"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
