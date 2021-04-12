package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.dto.NewDeskInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class NewDeskControllerTest {
  private DeskService deskService;
  private DeskAssembler deskAssembler;
  private NewDeskController controller;
  private User admin;

  @BeforeEach
  void setUp() {
    deskService = mock(DeskService.class);
    deskAssembler = mock(DeskAssembler.class);
    when(deskAssembler.setAuthorities(anySet())).thenReturn(deskAssembler);
    admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    controller = new NewDeskController(deskAssembler, deskService);
  }

  @Test
  void addDesk_validRequest() throws DeskNotAvailable {
    Set<NewDeskInfo> providedDesks =
        Set.of(new NewDeskInfo(5, 10), new NewDeskInfo(11, 40), new NewDeskInfo(1, 1));
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    List<DeskWithRoomName> expectedList =
        List.of(
            new DeskWithRoomName("roomName", "id1", 5, 10),
            new DeskWithRoomName("roomName", "id2", 11, 40),
            new DeskWithRoomName("roomName", "id3", 1, 1));
    when(deskAssembler.toCollectionModel(any()))
        .thenAnswer(
            invocation ->
                CollectionModel.of(
                    expectedList.stream().map(EntityModel::of).collect(Collectors.toList())));
    assertEquals(
        expectedList,
        controller.addDesk("roomName", admin, providedDesks).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }

  @Test
  void addDesk_deskIsNotAvailable() throws DeskNotAvailable {
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    NewDeskInfo faultyDesk = mock(NewDeskInfo.class);
    Set<NewDeskInfo> providedDesks =
        Set.of(mock(NewDeskInfo.class), faultyDesk, mock(NewDeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new DeskNotAvailable());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.addDesk("roomName", admin, providedDesks));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  void addDesk_providedRoomNotFound() throws DeskNotAvailable {
    User admin = mock(User.class);
    when(deskService.addDesk(any(), eq("roomName"))).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.addDesk(
                    "roomName", admin, Set.of(mock(NewDeskInfo.class), mock(NewDeskInfo.class))));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void addDesk_IllegalArgumentProvided() throws DeskNotAvailable {
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    NewDeskInfo faultyDesk = mock(NewDeskInfo.class);
    Set<NewDeskInfo> providedDesks =
        Set.of(mock(NewDeskInfo.class), faultyDesk, mock(NewDeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new IllegalArgumentException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.addDesk("roomName", admin, providedDesks));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }
}
