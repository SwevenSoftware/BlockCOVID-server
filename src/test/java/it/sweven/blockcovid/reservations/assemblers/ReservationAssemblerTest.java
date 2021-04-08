package it.sweven.blockcovid.reservations.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.reservations.entities.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationAssemblerTest {
  ReservationAssembler reservationAssembler;

  @BeforeEach
  void setUp() {
    reservationAssembler = new ReservationAssembler();
  }

  @Test
  void validEntityReturnsLinks() {
    assertTrue(reservationAssembler.toModel(mock(Reservation.class)).hasLink("new_reservation"));
  }
}
