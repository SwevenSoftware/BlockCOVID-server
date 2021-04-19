package it.sweven.blockcovid.reservations.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.sweven.blockcovid.reservations.entities.Reservation;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationWithRoomTest {
  private ReservationWithRoom reservation;

  @BeforeEach
  void setup() {
    reservation =
        new ReservationWithRoom(
            "reservationId",
            "deskId",
            "roomId",
            "username",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(18));
  }

  @Test
  void toReservation() {
    Reservation expectedReservation = reservation.toReservation();
    assertEquals(expectedReservation.getId(), reservation.getId());
    assertEquals(expectedReservation.getDeskId(), reservation.getDeskId());
    assertEquals(expectedReservation.getUsername(), reservation.getUsername());
    assertEquals(expectedReservation.getStart(), reservation.getStart());
    assertEquals(expectedReservation.getEnd(), reservation.getEnd());
  }
}
