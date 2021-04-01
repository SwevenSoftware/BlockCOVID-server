package it.sweven.blockcovid.controllers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.RoomService;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminNewRoomControllerTest {
  private RoomService roomService;
  private RoomAssembler roomAssembler;
  private AdminNewRoomController router;
  private User admin;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    roomAssembler = mock(RoomAssembler.class);
    router = new AdminNewRoomController(roomAssembler, roomService);
    admin = mock(User.class);
    when(admin.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
  }

  @Test
  void newRoom_requestWithValidRoomInfo() throws BadAttributeValueExpException {
    RoomInfo roomInfo =
        new RoomInfo(
            "testRoom", LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);

    Room fakeRoom = mock(Room.class);
    EntityModel fakeModel = mock(EntityModel.class);
    when(roomService.createRoom(any())).thenReturn(fakeRoom);
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    assertEquals(fakeModel, router.newRoom(admin, roomInfo));
  }

  @Test
  void newRoom_requestWithInvalidRoomInfo() throws BadAttributeValueExpException {
    RoomInfo roomInfo =
        new RoomInfo(
            null, LocalTime.of(8, 0), LocalTime.of(20, 0), Set.of(DayOfWeek.MONDAY), 10, 10);

    Room fakeRoom = mock(Room.class);
    EntityModel fakeModel = mock(EntityModel.class);
    when(roomService.createRoom(any())).thenThrow(new BadAttributeValueExpException(null));
    when(roomAssembler.toModel(any())).thenReturn(fakeModel);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.newRoom(admin, roomInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
