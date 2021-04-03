package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminDeleteDeskControllerTest {
  private DeskAssembler deskAssembler;
  private DeskService deskService;
  private AdminDeleteDeskController router;

  @BeforeEach
  void setUp() {
    deskAssembler = mock(DeskAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(deskAssembler)
        .toModel(any());
    deskService = mock(DeskService.class);
    router = new AdminDeleteDeskController(deskService, deskAssembler);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    DeskWithRoomName expected = new DeskWithRoomName("room", null, null);
    when(deskService.deleteDeskByInfosAndRoomName(any(), any())).thenReturn(fakeDesk);
    assertEquals(
        expected.getRoomName(),
        router.delete(mock(User.class), "room", mock(DeskInfo.class)).getContent().getRoomName());
  }

  @Test
  void roomNotFound() {
    when(deskService.deleteDeskByInfosAndRoomName(any(), any()))
        .thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.delete(mock(User.class), "room", mock(DeskInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void deskNotFound() {
    when(deskService.deleteDeskByInfosAndRoomName(any(), any()))
        .thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.delete(mock(User.class), "room", mock(DeskInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
