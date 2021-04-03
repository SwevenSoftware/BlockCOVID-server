package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.RoomBuilder;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminModifyRoomControllerTest {

  private RoomService roomService;
  private RoomAssembler roomAssembler;
  private AdminModifyRoomController router;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    doAnswer(invocation -> invocation.getArgument(0)).when(roomService).save(any());
    roomAssembler = mock(RoomAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(roomAssembler)
        .toModel(any());
    router = new AdminModifyRoomController(roomService, roomAssembler);
  }

  @Test
  void validEdit() throws BadAttributeValueExpException {
    Room fakeRoom = new Room();
    Room expected =
        new RoomBuilder()
            .name("name")
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(10)
            .height(10)
            .build();
    RoomInfo toChange =
        new RoomInfo(
            "name", LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, Set.of(DayOfWeek.MONDAY), 10, 10);
    when(roomService.getByName(anyString())).thenReturn(fakeRoom);
    assertEquals(expected, router.modifyRoom(mock(User.class), "room", toChange).getContent());
  }

  @Test
  void roomNotFound_throwsResponseStatusException() {
    when(roomService.getByName(any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> router.modifyRoom(mock(User.class), "room", mock(RoomInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
