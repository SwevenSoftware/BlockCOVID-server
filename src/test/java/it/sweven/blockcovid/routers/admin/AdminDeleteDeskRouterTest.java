package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.DeskAssembler;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.DeskWithRoomName;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.DeskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminDeleteDeskRouterTest {
  private DeskAssembler deskAssembler;
  private DeskService deskService;
  private AdminDeleteDeskRouter router;

  @BeforeEach
  void setUp() {
    deskAssembler = mock(DeskAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(deskAssembler)
        .toModel(any());
    deskService = mock(DeskService.class);
    router = new AdminDeleteDeskRouter(deskService, deskAssembler);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    DeskWithRoomName expected = new DeskWithRoomName(null, "room", null, null);
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
