package it.sweven.blockcovid.reservations.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ReservationWithRoomTest {

  @Test
  void getRealEnd() {
    ReservationWithRoom res1 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.MIN.withHour(15),
            LocalDateTime.MIN.withHour(16),
            null,
            null,
            false);
    assertNull(res1.getUsageEnd());

    ReservationWithRoom res2 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.MIN.withHour(10),
            LocalDateTime.MIN.withHour(17),
            LocalDateTime.MIN.withHour(11),
            null,
            false);
    assertEquals(LocalDateTime.MIN.withHour(17), res2.getUsageEnd());

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
    assertNull(res3.getUsageEnd());

    LocalDateTime realEnd4 = LocalDateTime.now().plusHours(1);
    ReservationWithRoom res4 =
        new ReservationWithRoom(
            "id1",
            "deskId1",
            "room1",
            "username2",
            LocalDateTime.now().minusHours(3),
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().minusHours(2),
            realEnd4,
            false);
    assertEquals(realEnd4, res4.getUsageEnd());
  }
}
