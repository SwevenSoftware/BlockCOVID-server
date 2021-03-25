package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.RoomService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminDeleteRoomRouterTest {
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private User submitter;
  private AdminDeleteRoomRouter router;

  @BeforeEach
  void setUp() {
    roomAssembler = mock(RoomAssembler.class);
    when(roomAssembler.setAuthorities(anySet())).thenReturn(roomAssembler);
    roomService = mock(RoomService.class);
    submitter = mock(User.class);
    when(submitter.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    router = new AdminDeleteRoomRouter(roomService, roomAssembler);
  }

  @Test
  void validDeletion() {
    Room fakeDeleted = mock(Room.class);
    when(roomService.deleteRoomByName(anyString())).thenReturn(fakeDeleted);
    when(roomService.getByName(anyString())).thenReturn(fakeDeleted);
    when(roomAssembler.toModel(any())).thenReturn(EntityModel.of(fakeDeleted));
    assertEquals(fakeDeleted, router.delete(submitter, "room").getContent());
  }

  @Test
  void roomNotFound_throwsResponseStatusException() {
    when(roomService.deleteRoomByName(anyString())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.delete(submitter, "room"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
