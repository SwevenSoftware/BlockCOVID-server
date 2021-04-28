package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class StartNewReservationControllerTest {
  private ReservationService reservationService;
  private StartNewReservationController controller;
  private ReservationWithRoom fakeReservation;
  private ReservationInfo fakeInfo;

  @BeforeEach
  void setUp()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException, StartingTooEarly {

    fakeInfo = mock(ReservationInfo.class);
    when(fakeInfo.getDeskId()).thenReturn("id1");
    when(fakeInfo.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(10));
    when(fakeInfo.getStart()).thenReturn(LocalDateTime.now());

    fakeReservation = mock(ReservationWithRoom.class);
    when(fakeReservation.getId()).thenReturn("id1");

    reservationService = mock(ReservationService.class);
    when(reservationService.addReservation(any(), any())).thenReturn(fakeReservation);
    when(reservationService.start(anyString(), any())).thenReturn(fakeReservation);

    ReservationWithRoomAssembler reservationWithRoomAssembler =
        mock(ReservationWithRoomAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(reservationWithRoomAssembler)
        .toModel(any());

    controller =
        new StartNewReservationController(reservationService, reservationWithRoomAssembler);
  }

  @Test
  void happyPath() {
    assertEquals(fakeReservation, controller.start(mock(User.class), fakeInfo).getContent());
  }

  @Test
  void nullReservationEnd_throwsResponseStatusException_BAD_REQUEST() {
    when(fakeInfo.getEnd()).thenReturn(null);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void endBeforeStart_throwsResponseStatusException_BAD_REQUEST() {
    when(fakeInfo.getEnd()).thenReturn(LocalDateTime.now().minusMinutes(15));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void nullReservationDeskId_throwsResponseStatusException_BAD_REQUEST() {
    when(fakeInfo.getDeskId()).thenReturn(null);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsReservationClash_throwsResponseStatusException_CONFLICT()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(reservationService.addReservation(any(), any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }

  @Test
  void serviceThrowsBadTimeIntervals_throwsResponseStatusException_BAD_REQUEST()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(reservationService.addReservation(any(), any())).thenThrow(new BadTimeIntervals());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsDeskNotFoundException_throwsResponseStatusException_BAD_REQUEST()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(reservationService.addReservation(any(), any())).thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsRoomNotFoundException_throwsResponseStatusException_BAD_REQUEST()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(reservationService.addReservation(any(), any())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsBadAttributeExpExpression_throwsResponseStatusException_BAD_REQUEST()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(reservationService.addReservation(any(), any()))
        .thenThrow(new BadAttributeValueExpException(""));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsNoSuchReservation_throwsResponseStatusException_NOT_FOUND()
      throws ReservationClash, StartingTooEarly, NoSuchReservation {
    when(reservationService.start(any(), any())).thenThrow(new NoSuchReservation());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void serviceThrowsStartingTooEarly_throwsResponseStatusException_TOO_EARLY()
      throws ReservationClash, StartingTooEarly, NoSuchReservation {
    when(reservationService.start(any(), any())).thenThrow(new StartingTooEarly());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.TOO_EARLY, thrown.getStatus());
  }

  @Test
  void serviceStartThrowsReservationClash_throwsResponseStatusException_CONFLICT()
      throws ReservationClash, StartingTooEarly, NoSuchReservation {
    when(reservationService.start(any(), any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.start(mock(User.class), fakeInfo));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }
}
