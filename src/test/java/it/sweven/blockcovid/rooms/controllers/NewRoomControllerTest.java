package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNameNotAvailable;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.Authority;
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

class NewRoomControllerTest {
  private RoomService roomService;
  private RoomAssembler roomAssembler;
  private NewRoomController controller;
  private User admin;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    roomAssembler = mock(RoomAssembler.class);
    controller = new NewRoomController(roomAssembler, roomService);
    admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
  }

  @Test
  void newRoom_requestWithValidRoomInfo()
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo roomInfo =
        new RoomInfo(
            "testRoom", LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);

    Room fakeRoom = mock(Room.class);
    EntityModel fakeModel = mock(EntityModel.class);
    when(roomService.createRoom(any())).thenReturn(fakeRoom);
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    assertEquals(fakeModel, controller.newRoom(admin, roomInfo));
  }

  @Test
  void newRoom_requestWithInvalidRoomInfo()
      throws BadAttributeValueExpException, RoomNameNotAvailable {
    RoomInfo roomInfo =
        new RoomInfo(
            null, LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);
    EntityModel fakeModel = mock(EntityModel.class);
    when(roomService.createRoom(any())).thenThrow(new BadAttributeValueExpException(null));
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.newRoom(admin, roomInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void newRoom_RoomNameNotAvailable() throws RoomNameNotAvailable, BadAttributeValueExpException {
    RoomInfo roomInfo =
        new RoomInfo(
            "name", LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);
    EntityModel fakeModel = mock(EntityModel.class);
    when(roomService.createRoom(any())).thenThrow(new RoomNameNotAvailable());
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.newRoom(admin, roomInfo));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }
}
