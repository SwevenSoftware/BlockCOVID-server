package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
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

class NewReservationControllerTest {
  private NewReservationController controller;
  private ReservationService service;
  private User testUser;
  private ReservationInfo info;

  @BeforeEach
  void setUp() throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    service = mock(ReservationService.class);
    when(service.addReservation(any(), any())).thenReturn(mock(ReservationWithRoom.class));
    ReservationWithRoomAssembler assembler = mock(ReservationWithRoomAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(assembler)
        .toModel(any());
    testUser = mock(User.class);
    info = mock(ReservationInfo.class);
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(5));
    when(info.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(15));
    when(info.getDeskId()).thenReturn("desk");
    controller = new NewReservationController(service, assembler);
  }

  @Test
  void validReservation() throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    ReservationWithRoom fakeReservation = mock(ReservationWithRoom.class);
    when(service.addReservation(any(), any())).thenReturn(fakeReservation);
    assertEquals(fakeReservation, controller.book(testUser, info).getContent());
  }

  @Test
  void serviceSignalsConflict_throwsResponseStatusException()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(service.addReservation(any(), any())).thenThrow(ReservationClash.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }

  @Test
  void serviceThrowsBadTimeIntervals_throwsResponseStatusException()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(service.addReservation(any(), any())).thenThrow(BadTimeIntervals.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsDeskNotFoundException_throwsResponseStatusException()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(service.addReservation(any(), any())).thenThrow(DeskNotFoundException.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceThrowsRoomNotFoundException_throwsResponseStatusException()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(service.addReservation(any(), any())).thenThrow(RoomNotFoundException.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void serviceThrowsBadAttributeValueExpException_throwsResponseStatusException()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    when(service.addReservation(any(), any())).thenThrow(BadAttributeValueExpException.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void reservationInfoNullStart_throwsResponseStatusException() {
    when(info.getStart()).thenReturn(null);
    assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
  }

  @Test
  void reservationInfoNullEnd_throwsResponseStatusException() {
    when(info.getEnd()).thenReturn(null);
    assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
  }

  @Test
  void reservationInfoNullDeskId_throwsResponseStatusException() {
    when(info.getDeskId()).thenReturn(null);
    assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
  }

  @Test
  void reservationInfoStartBeforeNow_throwsResponseStatusException() {
    when(info.getStart()).thenReturn(LocalDateTime.now().minusMinutes(10));
    assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
  }

  @Test
  void reservationInfoStartAfterEnd_throwsResponseStatusException() {
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(30));
    when(info.getEnd()).thenReturn(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
  }
}
