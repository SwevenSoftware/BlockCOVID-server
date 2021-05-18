package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNameNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ModifyRoomControllerTest {

  private RoomService roomService;
  private ModifyRoomController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    RoomAssembler roomAssembler = mock(RoomAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(roomAssembler)
        .toModel(any());
    controller = new ModifyRoomController(roomService, roomAssembler);
  }

  @Test
  void validEdit() throws BadAttributeValueExpException, RoomNameNotAvailable {
    Room expected = mock(Room.class);
    RoomInfo toChange =
        new RoomInfo(
            "name", LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, Set.of(DayOfWeek.MONDAY), 10, 10);
    when(roomService.updateRoom("room", toChange)).thenReturn(expected);
    assertEquals(expected, controller.modifyRoom(mock(User.class), "room", toChange).getContent());
  }

  @Test
  void roomNotFound_throwsResponseStatusException() throws RoomNameNotAvailable {
    when(roomService.updateRoom(any(), any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.modifyRoom(mock(User.class), "room", mock(RoomInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void roomNameNotAvailable_throwsResponseStatusException() throws RoomNameNotAvailable {
    when(roomService.updateRoom(any(), any())).thenThrow(new RoomNameNotAvailable());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.modifyRoom(mock(User.class), "room", mock(RoomInfo.class)));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }
}
