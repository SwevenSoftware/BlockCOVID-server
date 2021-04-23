package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.DeskModifyInfo;
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

class ModifyDeskControllerTest {

  private DeskService service;
  private ModifyDeskController controller;

  @BeforeEach
  void setUp() {
    service = mock(DeskService.class);
    DeskAssembler assembler = mock(DeskAssembler.class);
    when(assembler.toModel(any()))
        .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));
    controller = new ModifyDeskController(service, assembler);
  }

  @Test
  void modifyDesk() {
    DeskModifyInfo providedInfo =
        new DeskModifyInfo(new DeskInfo("id1", 5, 10), new DeskInfo("id1", 8, 7));
    Desk savedDesk = new Desk("id1", 5, 10, "roomName");
    when(service.getDeskByInfoAndRoomName(providedInfo.getOldInfo(), "roomName"))
        .thenReturn(savedDesk);
    when(service.update(savedDesk)).thenReturn(savedDesk);
    DeskWithRoomName expectedDesk = new DeskWithRoomName("roomName", "id1", 8, 7);
    assertEquals(
        expectedDesk,
        controller.modifyDesk(mock(User.class), "roomName", providedInfo).getContent());
  }

  @Test
  void modifyDesk_deskNotFound_throwsResponseStatusException() {
    when(service.getDeskByInfoAndRoomName(any(), anyString()))
        .thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.modifyDesk(mock(User.class), "roomName", mock(DeskModifyInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void modifyDesk_roomNotFound_throwsResponseStatusException() {
    when(service.getDeskByInfoAndRoomName(any(), anyString()))
        .thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.modifyDesk(mock(User.class), "roomName", mock(DeskModifyInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void modifyDesk_updatedDeskNotFound_throwsResponseStatusException() {
    when(service.getDeskByInfoAndRoomName(any(), anyString())).thenReturn(mock(Desk.class));
    when(service.update(any())).thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.modifyDesk(
                    mock(User.class),
                    "roomName",
                    new DeskModifyInfo(mock(DeskInfo.class), mock(DeskInfo.class))));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
