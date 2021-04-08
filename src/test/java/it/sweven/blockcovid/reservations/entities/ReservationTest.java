package it.sweven.blockcovid.reservations.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReservationTest {
  @Test
  void persistenceConstructionShouldSetTheId() {
    Reservation fakeRes =
        new Reservation(100L, "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX);
    assertEquals(100L, fakeRes.getId());
  }
}
