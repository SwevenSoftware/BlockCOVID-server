package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class DeleteDeskControllerTest {
  private DeskAssembler deskAssembler;
  private DeskService deskService;
  private DeleteDeskController controller;

  @BeforeEach
  void setUp() {
    deskAssembler = mock(DeskAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(deskAssembler)
        .toModel(any());
    deskService = mock(DeskService.class);
    controller = new DeleteDeskController(deskService, deskAssembler);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    when(fakeDesk.getId()).thenReturn("idFakeDesk");
    DeskInfo providedDeskInfo = mock(DeskInfo.class);
    DeskWithRoomName expected = new DeskWithRoomName("room", null, null);
    when(deskService.getDeskByInfoAndRoomName(providedDeskInfo, "room")).thenReturn(fakeDesk);
    when(deskService.deleteDeskById("idFakeDesk")).thenReturn(fakeDesk);
    assertEquals(
        expected.getRoomName(),
        controller.delete(mock(User.class), "room", providedDeskInfo).getContent().getRoomName());
  }

  @Test
  void roomNotFound() {
    when(deskService.getDeskByInfoAndRoomName(any(), any())).thenReturn(mock(Desk.class));
    when(deskService.deleteDeskById(any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.delete(mock(User.class), "room", mock(DeskInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void deskNotFound() {
    when(deskService.getDeskByInfoAndRoomName(any(), any())).thenReturn(mock(Desk.class));
    when(deskService.deleteDeskById(any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.delete(mock(User.class), "room", mock(DeskInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
