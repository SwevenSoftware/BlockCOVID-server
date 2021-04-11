package it.sweven.blockcovid.reservations.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.reservations.entities.Reservation;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class ReservationAssemblerTest {
  ReservationAssembler reservationAssembler;

  @BeforeEach
  void setUp() {
    reservationAssembler = new ReservationAssembler();
  }

  @Test
  void validEntityReturnsLinks() {
    EntityModel<Reservation> returned = reservationAssembler.toModel(mock(Reservation.class));
    assertTrue(returned.hasLink("new_reservation"));
    assertTrue(returned.hasLink("modify_reservation"));
    assertTrue(returned.hasLink("desk_status_reservation"));
    assertTrue(returned.hasLink("delete_reservation"));
  }

  @Test
  void collectionModelHasSelfRel() {
    assertTrue(reservationAssembler.toCollectionModel(Collections.emptyList()).hasLink("self"));
  }
}
