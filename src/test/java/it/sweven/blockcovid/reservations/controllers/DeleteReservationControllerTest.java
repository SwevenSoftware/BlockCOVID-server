package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationAssembler;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class DeleteReservationControllerTest {

  private ReservationService service;
  private DeleteReservationController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    ReservationAssembler assembler = mock(ReservationAssembler.class);
    when(assembler.toModel(any()))
        .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));
    controller = new DeleteReservationController(service, assembler);
  }

  @Test
  void deleteReservation_validRequest() {
    User user = mock(User.class);
    when(user.isUser()).thenReturn(true);
    when(user.getUsername()).thenReturn("username");
    Reservation fakeReservation = mock(Reservation.class);
    when(fakeReservation.getUsername()).thenReturn("username");
    when(service.findById("idReservation")).thenReturn(fakeReservation);
    when(service.delete("idReservation")).thenReturn(fakeReservation);
    assertEquals(fakeReservation, controller.deleteReservation(user, "idReservation").getContent());
    verify(service).delete("idReservation");
  }

  @Test
  void deleteReservation_idNotFound_throwsResponseStatusException() {
    when(service.findById(anyString())).thenThrow(new NoSuchReservation());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.deleteReservation(mock(User.class), "idReservation"));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteReservation_usernameDoesntMatch_throwsResponseStatusException() {
    User user = mock(User.class);
    when(user.isUser()).thenReturn(true);
    when(user.getUsername()).thenReturn("username");
    Reservation fakeReservation = mock(Reservation.class);
    when(fakeReservation.getUsername()).thenReturn("another");
    when(service.findById(anyString())).thenReturn(fakeReservation);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.deleteReservation(user, "idReservation"));
    assertEquals(thrown.getStatus(), HttpStatus.UNAUTHORIZED);
  }

  @Test
  void deleteReservation_adminCanDeleteEverything() {
    User user = mock(User.class);
    when(user.isUser()).thenReturn(false);
    when(user.getUsername()).thenReturn("admin");
    Reservation fakeReservation = mock(Reservation.class);
    when(fakeReservation.getUsername()).thenReturn("username");
    when(service.findById("idReservation")).thenReturn(fakeReservation);
    when(service.delete("idReservation")).thenReturn(fakeReservation);
    assertEquals(fakeReservation, controller.deleteReservation(user, "idReservation").getContent());
    verify(service).delete("idReservation");
  }
}
