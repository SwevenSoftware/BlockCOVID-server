package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

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
    doAnswer(
            invocation -> {
              ArrayList<DeskWithRoomName> get = invocation.getArgument(0);
              return CollectionModel.of(
                  get.stream()
                      .map(desk -> deskAssembler.toModel(desk))
                      .collect(Collectors.toList()));
            })
        .when(deskAssembler)
        .toCollectionModel(any());
    deskService = mock(DeskService.class);
    controller = new DeleteDeskController(deskService, deskAssembler);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    Room fakeRoom = mock(Room.class);
    when(fakeRoom.getName()).thenReturn("room");
    when(fakeDesk.getId()).thenReturn("id1");
    DeskWithRoomName expected = new DeskWithRoomName("room", "id1", null, null, Status.CLEAN);
    when(deskService.getDeskById(any())).thenReturn(fakeDesk);
    when(deskService.getRoom(any())).thenReturn(fakeRoom);
    when(deskService.deleteDeskById(any())).thenReturn(fakeDesk);
    assertTrue(
        controller.delete(mock(User.class), List.of(fakeDesk.getId())).getContent().stream()
            .anyMatch(obj -> obj.getContent().getRoomName().equals(expected.getRoomName())));
  }

  @Test
  void deskNotFound() {
    when(deskService.getDeskByInfoAndRoomName(any(), any())).thenReturn(mock(Desk.class));
    when(deskService.getDeskById(any())).thenThrow(new DeskNotFoundException());
    assertTrue(controller.delete(mock(User.class), List.of("WrongId")).getContent().isEmpty());
  }

  @Test
  void roomNotFound() {
    when(deskService.getDeskById(any())).thenReturn(mock(Desk.class));
    when(deskService.getRoom(any())).thenThrow(new RoomNotFoundException());
    assertTrue(controller.delete(mock(User.class), List.of("WrongId")).getContent().isEmpty());
  }
}
