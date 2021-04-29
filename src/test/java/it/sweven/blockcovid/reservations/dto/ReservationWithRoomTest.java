package it.sweven.blockcovid.reservations.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ReservationWithRoomTest {

  @Test
  void isEnded() {
    ReservationWithRoom res1 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().minusHours(2),
            null,
            false);
    assertTrue(res1.isEnded());

    ReservationWithRoom res2 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now().plusHours(1),
            false);
    assertTrue(res2.isEnded());

    ReservationWithRoom res3 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().minusHours(2),
            null,
            false);
    assertFalse(res3.isEnded());
  }
}
