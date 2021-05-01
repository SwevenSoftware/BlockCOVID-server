package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class StartReservationControllerTest {
  private ReservationWithRoom fakeReservation;
  private ReservationService reservationService;
  private StartReservationController controller;
  private User fakeUser;

  @BeforeEach
  void setUp() throws ReservationClash, StartingTooEarly {

    fakeUser = mock(User.class);
    when(fakeUser.getUsername()).thenReturn("user");

    fakeReservation = mock(ReservationWithRoom.class);
    when(fakeReservation.getId()).thenReturn("id1");
    when(fakeReservation.getUsername()).thenReturn("user");
    when(fakeReservation.getUsageEnd()).thenReturn(LocalDateTime.MAX);

    reservationService = mock(ReservationService.class);
    when(reservationService.findById(any())).thenReturn(fakeReservation);
    when(reservationService.start(anyString(), any())).thenReturn(fakeReservation);

    ReservationWithRoomAssembler reservationWithRoomAssembler =
        mock(ReservationWithRoomAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(reservationWithRoomAssembler)
        .toModel(any());

    controller = new StartReservationController(reservationService, reservationWithRoomAssembler);
  }

  @Test
  void happyPath() {
    assertEquals(fakeReservation, controller.start(fakeUser, "id1").getContent());
  }

  @Test
  void submitterNotOwningReservation() {
    when(fakeUser.getUsername()).thenReturn("Not owner");
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
  }

  @Test
  void notSuchReservation() throws ReservationClash, StartingTooEarly {
    when(reservationService.start(any(), any())).thenThrow(new NoSuchReservation());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void startingTooEarly() throws ReservationClash, StartingTooEarly {
    when(reservationService.start(any(), any())).thenThrow(new StartingTooEarly());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void reservationClash() throws ReservationClash, StartingTooEarly {
    when(reservationService.start(any(), any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }

  @Test
  void startingAlreadyStartedReservation_throwsResponseStatusException() {
    when(fakeReservation.getUsageStart()).thenReturn(LocalDateTime.now().minusMinutes(15));
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void startingAlreadyEndedReservation_throwsResponseStatusException() {
    when(fakeReservation.isEnded()).thenReturn(true);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.start(fakeUser, "id1"));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
