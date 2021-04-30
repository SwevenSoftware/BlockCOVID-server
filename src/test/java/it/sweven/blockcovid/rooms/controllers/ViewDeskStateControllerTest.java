package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ViewDeskStateControllerTest {
  private DeskService deskService;
  private DeskAssembler assembler;
  private ViewDeskStateController controller;

  @BeforeEach
  void setUp() {
    deskService = mock(DeskService.class);
    assembler = mock(DeskAssembler.class);
    controller = new ViewDeskStateController(deskService, assembler);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    Room fakeRoom = mock(Room.class);
    when(fakeRoom.getName()).thenReturn("roomName");
    when(fakeDesk.getX()).thenReturn(5);
    when(fakeDesk.getY()).thenReturn(6);
    when(fakeDesk.getDeskStatus()).thenReturn(Status.CLEAN);
    when(deskService.getDeskById("deskId")).thenReturn(fakeDesk);
    when(deskService.getRoom("deskId")).thenReturn(fakeRoom);
    DeskWithRoomName expectedDesk = new DeskWithRoomName("roomName", "deskId", 5, 6, Status.CLEAN);
    when(assembler.toModel(expectedDesk)).thenReturn(EntityModel.of(expectedDesk));
    assertEquals(expectedDesk, controller.deskState(mock(User.class), "deskId").getContent());
  }

  @Test
  void deskNotFound_throwsDeskNotFoundException() {
    when(deskService.getDeskById(any())).thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.deskState(mock(User.class), "id"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void roomNotFound_throwsRoomNotFoundException() {
    when(deskService.getDeskById("id")).thenReturn(mock(Desk.class));
    when(deskService.getRoom(any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.deskState(mock(User.class), "id"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
