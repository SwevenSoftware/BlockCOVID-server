package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.reservations.dto.DeskAvailability;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.NoNextReservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetDeskStateAtTimeControllerTest {
  private ReservationService reservationService;
  private GetDeskStateAtTimeController controller;

  @BeforeEach
  void setUp() {
    reservationService = mock(ReservationService.class);
    controller = new GetDeskStateAtTimeController(reservationService);
  }

  @Test
  void validDeskAndTime() throws NoNextReservation {
    ReservationWithRoom fakeReservation = mock(ReservationWithRoom.class);
    when(fakeReservation.getStart()).thenReturn(LocalDateTime.now().plusHours(2));
    when(reservationService.nextReservation(any(), any())).thenReturn(Optional.of(fakeReservation));
    when(reservationService.findIfTimeFallsInto(any(), any())).thenReturn(Optional.empty());
    DeskAvailability returned =
        controller.getDeskState(mock(User.class), LocalDateTime.MIN, "id").getContent();
    assert returned != null;
    assertTrue(returned.isAvailable());
    assertEquals(fakeReservation.getStart(), returned.getNextChange());
  }

  @Test
  void getDeskState_timestampInsideReservation() {
    ReservationWithRoom fakeReservation = mock(ReservationWithRoom.class);
    when(fakeReservation.getEnd()).thenReturn(LocalDateTime.now().plusHours(2));
    when(reservationService.nextReservation(any(), any())).thenReturn(Optional.of(fakeReservation));
    when(reservationService.findIfTimeFallsInto(any(), any()))
        .thenReturn(Optional.of(fakeReservation));
    DeskAvailability returned =
        controller.getDeskState(mock(User.class), LocalDateTime.MIN, "id").getContent();
    assert returned != null;
    assertFalse(returned.isAvailable());
    assertEquals(fakeReservation.getEnd(), returned.getNextChange());
  }

  @Test
  void noNextReservationStillValid() {
    when(reservationService.nextReservation(any(), any())).thenReturn(Optional.empty());
    when(reservationService.findIfTimeFallsInto(any(), any())).thenReturn(Optional.empty());
    DeskAvailability returned =
        controller.getDeskState(mock(User.class), LocalDateTime.MIN, "id").getContent();
    assert returned != null;
    assertTrue(returned.isAvailable());
    assertNull(returned.getNextChange());
  }
}
