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

  @Test
  void comparisonIsDoneOnTheStartingTIme() {
    Reservation fakeRes1 =
        new Reservation(
            100L, "deskId", "username", LocalDateTime.MIN.plusMinutes(5), LocalDateTime.MAX);
    Reservation fakeRes2 =
        new Reservation(
            100L, "deskId", "username", LocalDateTime.MIN.plusMinutes(10), LocalDateTime.MAX);
    Reservation fakeRes3 =
        new Reservation(100L, "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX);
    Reservation fakeRes4 =
        new Reservation(
            100L,
            "deskId",
            "username",
            LocalDateTime.MIN.plusMinutes(10),
            LocalDateTime.MAX.minusMinutes(10));
    assertEquals(-1, fakeRes1.compareTo(fakeRes2));
    assertEquals(1, fakeRes1.compareTo(fakeRes3));
    assertEquals(0, fakeRes2.compareTo(fakeRes4));
  }
}
