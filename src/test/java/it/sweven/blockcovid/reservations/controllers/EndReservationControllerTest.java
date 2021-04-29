package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.EndUsageInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.AlreadyEnded;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class EndReservationControllerTest {

  private ReservationService service;
  private ReservationWithRoomAssembler assembler;
  private EndReservationController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    assembler = mock(ReservationWithRoomAssembler.class);
    controller = new EndReservationController(service, assembler);
  }

  @Test
  void reservationNotFound_serviceFind() {
    when(service.findById(any())).thenThrow(new NoSuchReservation());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(mock(User.class), "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void submitterDoesntOwnReservation() {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user2");
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(submitter, "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
  }

  @Test
  void reservationNotFound_serviceEnd() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    when(service.end(any(), any(), any())).thenThrow(new NoSuchReservation());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(submitter, "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void reservationEndBeforeStart() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    when(service.end(any(), any(), any())).thenThrow(new BadTimeIntervals());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(submitter, "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void reservationAlreadyEnded() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    when(service.end(any(), any(), any())).thenThrow(new AlreadyEnded());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(submitter, "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void reservationClash() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    when(service.end(any(), any(), any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.endUsage(submitter, "reservationId", mock(EndUsageInfo.class)));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }

  @Test
  void validEnd_deskCleaned() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    ReservationWithRoom expectedReservation = mock(ReservationWithRoom.class);
    when(service.end(eq("reservationId"), any(), eq(true))).thenReturn(expectedReservation);
    when(assembler.toModel(expectedReservation)).thenReturn(EntityModel.of(expectedReservation));
    assertEquals(
        controller.endUsage(submitter, "reservationId", new EndUsageInfo(true)).getContent(),
        expectedReservation);
  }

  @Test
  void validEnd_deskNotCleaned() throws ReservationClash, AlreadyEnded, BadTimeIntervals {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById("reservationId")).thenReturn(mockReservation);
    User submitter = mock(User.class);
    when(submitter.getUsername()).thenReturn("user1");
    when(mockReservation.getUsername()).thenReturn("user1");
    ReservationWithRoom expectedReservation = mock(ReservationWithRoom.class);
    when(service.end(eq("reservationId"), any(), eq(false))).thenReturn(expectedReservation);
    when(assembler.toModel(expectedReservation)).thenReturn(EntityModel.of(expectedReservation));
    assertEquals(
        expectedReservation,
        controller.endUsage(submitter, "reservationId", new EndUsageInfo(false)).getContent());
    assertEquals(
        expectedReservation,
        controller.endUsage(submitter, "reservationId", new EndUsageInfo(null)).getContent());
  }
}
