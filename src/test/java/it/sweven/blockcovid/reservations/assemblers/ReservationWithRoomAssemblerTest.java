package it.sweven.blockcovid.reservations.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class ReservationWithRoomAssemblerTest {
  ReservationWithRoomAssembler reservationWithRoomAssembler;

  @BeforeEach
  void setUp() {
    reservationWithRoomAssembler = new ReservationWithRoomAssembler();
  }

  @Test
  void validEntityReturnsLinks() {
    EntityModel<ReservationWithRoom> returned =
        reservationWithRoomAssembler.toModel(mock(ReservationWithRoom.class));
    assertTrue(returned.hasLink("new_reservation"));
    assertTrue(returned.hasLink("modify_reservation"));
    assertTrue(returned.hasLink("desk_status_reservation"));
    assertTrue(returned.hasLink("delete_reservation"));
  }

  @Test
  void collectionModelHasSelfRel() {
    assertTrue(
        reservationWithRoomAssembler.toCollectionModel(Collections.emptyList()).hasLink("self"));
  }
}
