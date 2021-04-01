package it.sweven.blockcovid.controllers.cleaner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.room.Status;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CleanerMarkRoomAsCleanRouterTest {
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private CleanerMarkRoomAsCleanController router;

  @BeforeEach
  void setUp() {
    roomAssembler = mock(RoomAssembler.class);
    when(roomAssembler.setAuthorities(any())).thenReturn(roomAssembler);
    roomService = mock(RoomService.class);
    router = new CleanerMarkRoomAsCleanController(roomAssembler, roomService);
  }

  @Test
  void validMark() {
    User cleaner = mock(User.class);
    Room room = mock(Room.class);
    when(roomService.getByName("room")).thenReturn(room);
    when(roomService.save(any())).thenReturn(room);
    when(roomAssembler.toModel(any())).thenReturn(EntityModel.of(room));
    assertEquals(room, router.markAsClean(cleaner, "room").getContent());
  }

  @Test
  void invalidRoomName_throwsResponseStatusException() {
    when(roomService.setStatus("room", Status.CLEAN)).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.markAsClean(mock(User.class), "room"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
