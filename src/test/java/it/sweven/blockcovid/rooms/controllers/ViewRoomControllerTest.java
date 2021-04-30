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
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ViewRoomControllerTest {
  private RoomService roomService;
  private DeskService deskService;
  private ReservationService reservationService;
  private RoomWithDesksAssembler assembler;
  private ViewRoomController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    deskService = mock(DeskService.class);
    reservationService = mock(ReservationService.class);
    assembler = mock(RoomWithDesksAssembler.class);
    controller = new ViewRoomController(roomService, deskService, reservationService, assembler);
  }

  @Test
  void viewRoom_existingRoom() {
    Room expectedRoom = mock(Room.class);
    when(roomService.getByName("roomName")).thenReturn(expectedRoom);
    List<Desk> expectedDesks =
        List.of(
            new Desk("desk0", 3, 45, "roomId", Status.CLEAN),
            new Desk("desk1", 20, 11, "roomId", Status.CLEAN));
    when(deskService.getDesksByRoom("roomName")).thenReturn(expectedDesks);
    when(reservationService.timeConflict(eq("desk0"), any(), any())).thenReturn(true);
    when(reservationService.timeConflict(eq("desk1"), any(), any())).thenReturn(false);
    RoomWithDesks expectedRoomWithRoom =
        new RoomWithDesks(
            expectedRoom,
            List.of(
                new DeskInfoAvailability("desk0", 3, 45, false),
                new DeskInfoAvailability("desk1", 20, 11, true)));
    EntityModel<RoomWithDesks> expectedEntityModel = EntityModel.of(expectedRoomWithRoom);
    when(assembler.toModel(expectedRoomWithRoom)).thenReturn(expectedEntityModel);
    assertEquals(
        expectedEntityModel,
        controller.viewRoom(
            mock(User.class),
            "roomName",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(16)));
  }

  @Test
  void viewRoom_fromAfterTo_throwsRoomNotFoundException() {
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.viewRoom(
                    mock(User.class),
                    "roomName",
                    LocalDateTime.now().withHour(16),
                    LocalDateTime.now().withHour(12)));
    assertEquals(thrown.getStatus(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void viewRoom_nonExistingRoom_throwsRoomNotFoundException() {
    when(roomService.getByName("roomName")).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.viewRoom(
                    mock(User.class),
                    "roomName",
                    LocalDateTime.now().withHour(15),
                    LocalDateTime.now().withHour(16)));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }
}
