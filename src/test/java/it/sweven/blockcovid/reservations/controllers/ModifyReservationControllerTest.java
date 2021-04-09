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
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ModifyReservationControllerTest {

  private ReservationService service;
  private ModifyReservationController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    ReservationAssembler assembler = mock(ReservationAssembler.class);
    when(assembler.toModel(any()))
        .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));
    controller = new ModifyReservationController(service, assembler);
  }

  @Test
  void modifyReservation_validRequest() throws ReservationClash {
    ReservationInfo providedInfo =
        new ReservationInfo(
            "idDesk", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(120));
    Reservation fakeReservation = mock(Reservation.class);
    when(service.findById("idReservation")).thenReturn(Optional.of(fakeReservation));
    when(service.save(fakeReservation)).thenReturn(fakeReservation);
    assertEquals(
        fakeReservation,
        controller.modifyReservation(mock(User.class), "idReservation", providedInfo).getContent());
    verify(fakeReservation).setDeskId(providedInfo.getDeskId());
    verify(fakeReservation).setStart(providedInfo.getStart());
    verify(fakeReservation).setEnd(providedInfo.getEnd());
  }

  @Test
  void modifyReservation_reservationNotFound_throwsResponseStatusException() {
    when(service.findById(anyString())).thenReturn(Optional.empty());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.modifyReservation(
                    mock(User.class), "idReservation", mock(ReservationInfo.class)));
    assertEquals(thrown.getStatus(), HttpStatus.NOT_FOUND);
  }

  @Test
  void modifyReservation_conflictNewReservation_throwsResponseStatusException()
      throws ReservationClash {
    when(service.findById(anyString())).thenReturn(Optional.of(mock(Reservation.class)));
    when(service.save(any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.modifyReservation(
                    mock(User.class), "idReservation", mock(ReservationInfo.class)));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }
}
