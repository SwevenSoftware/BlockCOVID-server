package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.rooms.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfoAvailability;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ListRoomsControllerTest {

  private RoomService roomService;
  private DeskService deskService;
  private ReservationService reservationService;
  private RoomWithDesksAssembler assembler;
  private ListRoomsController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    deskService = mock(DeskService.class);
    reservationService = mock(ReservationService.class);
    assembler = mock(RoomWithDesksAssembler.class);
    controller = new ListRoomsController(roomService, deskService, reservationService, assembler);
  }

  @Test
  void listRooms() {
    List<Room> rooms = List.of(mock(Room.class), mock(Room.class));
    when(rooms.get(0).getName()).thenReturn("room0");
    when(rooms.get(1).getName()).thenReturn("room1");
    when(roomService.getAllRooms()).thenReturn(rooms);
    when(deskService.getDesksByRoom("room0")).thenReturn(List.of(new Desk("desk0", 2, 4, "room0")));
    when(deskService.getDesksByRoom("room1")).thenReturn(List.of(new Desk("desk1", 3, 7, "room1")));
    when(reservationService.timeConflict(eq("desk0"), any(), any())).thenReturn(true);
    when(reservationService.timeConflict(eq("desk1"), any(), any())).thenReturn(false);
    List<RoomWithDesks> roomsWithDesks =
        List.of(
            new RoomWithDesks(
                rooms.get(0), List.of(new DeskInfoAvailability("desk0", 2, 4, false))),
            new RoomWithDesks(
                rooms.get(1), List.of(new DeskInfoAvailability("desk1", 3, 7, true))));
    CollectionModel<EntityModel<RoomWithDesks>> expectedCollection =
        CollectionModel.of(
            roomsWithDesks.stream().map(EntityModel::of).collect(Collectors.toList()));
    when(assembler.toCollectionModel(roomsWithDesks)).thenReturn(expectedCollection);
    assertEquals(
        expectedCollection,
        controller.listRooms(
            mock(User.class), LocalDateTime.now().withHour(15), LocalDateTime.now().withHour(17)));
  }

  @Test
  void viewRoom_fromAfterTo_throwsRoomNotFoundException() {
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.listRooms(
                    mock(User.class),
                    LocalDateTime.now().withHour(16),
                    LocalDateTime.now().withHour(12)));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }
}
