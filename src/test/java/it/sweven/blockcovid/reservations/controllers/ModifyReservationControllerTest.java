package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.entities.ReservationBuilder;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ModifyReservationControllerTest {

  private ReservationService service;
  private ModifyReservationController controller;
  private ReservationBuilder builder;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    ReservationWithRoomAssembler assembler = mock(ReservationWithRoomAssembler.class);
    when(assembler.toModel(any()))
        .thenAnswer(invocation -> EntityModel.of(invocation.getArgument(0)));
    builder = spy(new ReservationBuilder());
    controller = new ModifyReservationController(service, assembler, builder);
  }

  @Test
  void modifyReservation_validRequest() throws ReservationClash, BadAttributeValueExpException {
    User user = mock(User.class);
    when(user.isUser()).thenReturn(true);
    when(user.getUsername()).thenReturn("username");
    ReservationInfo providedInfo =
        new ReservationInfo(
            "idDesk", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(120));
    ReservationWithRoom fakeReservation = mock(ReservationWithRoom.class);
    Reservation reservationToSave = mock(Reservation.class);
    doReturn(reservationToSave).when(builder).build();
    when(fakeReservation.getUsername()).thenReturn("username");
    when(service.findById("idReservation")).thenReturn(fakeReservation);
    when(service.save(reservationToSave)).thenReturn(fakeReservation);
    assertEquals(
        fakeReservation,
        controller.modifyReservation(user, "idReservation", providedInfo).getContent());
    verify(reservationToSave).setDeskId(providedInfo.getDeskId());
    verify(reservationToSave).setStart(providedInfo.getStart());
    verify(reservationToSave).setEnd(providedInfo.getEnd());
    verify(service).save(reservationToSave);
  }

  @Test
  void modifyReservation_reservationNotFound_throwsResponseStatusException() {
    when(service.findById(anyString())).thenThrow(new NoSuchReservation());
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
      throws ReservationClash, BadAttributeValueExpException {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    doReturn(mock(Reservation.class)).when(builder).build();
    when(service.findById(anyString())).thenReturn(mockReservation);
    when(service.save(any())).thenThrow(new ReservationClash());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.modifyReservation(
                    mock(User.class), "idReservation", mock(ReservationInfo.class)));
    assertEquals(thrown.getStatus(), HttpStatus.CONFLICT);
  }

  @Test
  void modifyReservation_usernameDoesntMatch_throwsResponseStatusException() {
    User user = mock(User.class);
    when(user.isUser()).thenReturn(true);
    when(user.getUsername()).thenReturn("username");
    ReservationWithRoom fakeReservation = mock(ReservationWithRoom.class);
    when(fakeReservation.getUsername()).thenReturn("another");
    when(service.findById(anyString())).thenReturn(fakeReservation);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.modifyReservation(user, "idReservation", mock(ReservationInfo.class)));
    assertEquals(thrown.getStatus(), HttpStatus.UNAUTHORIZED);
  }

  @Test
  void modifyReservation_reservationNotCorrectlyBuilt_throwsResponseStatusException()
      throws BadAttributeValueExpException {
    ReservationWithRoom mockReservation = mock(ReservationWithRoom.class);
    when(service.findById(anyString())).thenReturn(mockReservation);
    doThrow(new BadAttributeValueExpException("")).when(builder).build();
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () ->
                controller.modifyReservation(
                    mock(User.class), "idReservation", mock(ReservationInfo.class)));
    assertEquals(thrown.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
