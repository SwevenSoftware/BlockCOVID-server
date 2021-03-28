package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.assemblers.DeskAssembler;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.DeskWithRoomName;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.DeskService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminNewDeskRouterTest {
  private DeskService deskService;
  private DeskAssembler deskAssembler;
  private AdminNewDeskRouter router;
  private User admin;

  @BeforeEach
  void setUp() {
    deskService = mock(DeskService.class);
    deskAssembler = mock(DeskAssembler.class);
    when(deskAssembler.setAuthorities(anySet())).thenReturn(deskAssembler);
    admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    router = new AdminNewDeskRouter(deskAssembler, deskService);
  }

  @Test
  void addDesk_validRequest() throws DeskNotAvailable {
    Set<DeskInfo> providedDesks =
        Set.of(new DeskInfo(5, 10), new DeskInfo(11, 40), new DeskInfo(1, 1));
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    List<DeskWithRoomName> expectedList =
        List.of(
            new DeskWithRoomName("roomName", 5, 10),
            new DeskWithRoomName("roomName", 11, 40),
            new DeskWithRoomName("roomName", 1, 1));
    when(deskAssembler.toCollectionModel(any()))
        .thenAnswer(
            invocation ->
                CollectionModel.of(
                    expectedList.stream().map(EntityModel::of).collect(Collectors.toList())));
    assertEquals(
        expectedList,
        router.addDesk("roomName", admin, providedDesks).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }

  @Test
  void addDesk_deskIsNotAvailable() throws DeskNotAvailable {
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    DeskInfo faultyDesk = mock(DeskInfo.class);
    Set<DeskInfo> providedDesks = Set.of(mock(DeskInfo.class), faultyDesk, mock(DeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new DeskNotAvailable());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.addDesk("roomName", admin, providedDesks));
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
                router.addDesk(
                    "roomName", admin, Set.of(mock(DeskInfo.class), mock(DeskInfo.class))));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void addDesk_IllegalArgumentProvided() throws DeskNotAvailable {
    when(deskService.addDesk(any(), eq("roomName"))).thenReturn(mock(Desk.class));
    DeskInfo faultyDesk = mock(DeskInfo.class);
    Set<DeskInfo> providedDesks = Set.of(mock(DeskInfo.class), faultyDesk, mock(DeskInfo.class));
    when(deskService.addDesk(faultyDesk, "roomName")).thenThrow(new IllegalArgumentException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.addDesk("roomName", admin, providedDesks));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }
}
