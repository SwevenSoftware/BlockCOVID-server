package it.sweven.blockcovid.reservations.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class StartReservationControllerTest {
  private ReservationWithRoom fakeReservation;
  private ReservationService reservationService;
  private StartReservationController controller;

  @BeforeEach
  void setUp()
      throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException, StartingTooEarly {

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

    controller = new StartReservationController(reservationService, reservationWithRoomAssembler);
  }

  @Test
  void happyPath() {}
}
