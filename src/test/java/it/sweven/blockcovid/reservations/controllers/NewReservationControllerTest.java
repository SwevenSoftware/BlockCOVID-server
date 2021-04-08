package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class NewReservationControllerTest {
  private NewReservationController controller;
  private ReservationService service;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    ReservationAssembler assembler = mock(ReservationAssembler.class);
    doAnswer(invocation -> EntityModel.of(invocation.getArgument(0)))
        .when(assembler)
        .toModel(any());
    controller = new NewReservationController(service, assembler);
  }

  @Test
  void validReservation() throws ReservationClash {
    User testUser = mock(User.class);
    ReservationInfo info = mock(ReservationInfo.class);
    Reservation fakeReservation = mock(Reservation.class);
    when(info.isValid()).thenReturn(true);
    when(service.addReservation(any(), any())).thenReturn(fakeReservation);
    assertEquals(fakeReservation, controller.book(testUser, info).getContent());
  }

  @Test
  void invalidInfoProvided_throwsResponseStatusException() throws ReservationClash {
    User testUser = mock(User.class);
    ReservationInfo info = mock(ReservationInfo.class);
    when(info.isValid()).thenReturn(false);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }

  @Test
  void serviceSignalsConflict_throwsResponseStatusException() throws ReservationClash {
    User testUser = mock(User.class);
    ReservationInfo info = mock(ReservationInfo.class);
    when(info.isValid()).thenReturn(true);
    when(service.addReservation(any(), any())).thenThrow(ReservationClash.class);
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.book(testUser, info));
    assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
  }
}
