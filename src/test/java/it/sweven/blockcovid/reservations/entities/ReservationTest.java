package it.sweven.blockcovid.reservations.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReservationTest {
  @Test
  void persistenceConstructionShouldSetTheId() {
    Reservation fakeRes =
        new Reservation(
            "id1", "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX, null, null, false);
    assertEquals("id1", fakeRes.getId());
  }

  @Test
  void comparisonIsDoneOnTheStartingTIme() {
    Reservation fakeRes1 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.MIN.plusMinutes(5),
            LocalDateTime.MAX,
            null,
            null,
            false);
    Reservation fakeRes2 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.MIN.plusMinutes(10),
            LocalDateTime.MAX,
            null,
            null,
            false);
    Reservation fakeRes3 =
        new Reservation(
            "id1", "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX, null, null, false);
    Reservation fakeRes4 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.MIN.plusMinutes(10),
            LocalDateTime.MAX.minusMinutes(10),
            null,
            null,
            false);
    assertEquals(-1, fakeRes1.compareTo(fakeRes2));
    assertEquals(1, fakeRes1.compareTo(fakeRes3));
    assertEquals(0, fakeRes2.compareTo(fakeRes4));
  }

  @Test
  void reservationNeverClashesWithHerself() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(10),
            null,
            null,
            false);
    Reservation res2 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.now().minusMinutes(3),
            LocalDateTime.now().plusMinutes(10),
            null,
            null,
            false);
    assertFalse(res1.clashesWith(res2));
  }

  @Test
  void reservationClashesOnlyIfSameDesk() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId1",
            "username",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId2",
            "username",
            LocalDateTime.now().withHour(14),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    assertFalse(res1.clashesWith(res2));
  }

  @Test
  void reservationClashesIfTimeIntervalsOverlap() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.now().withHour(14),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    assertTrue(res1.clashesWith(res2));

    Reservation res3 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.now().withHour(14),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    Reservation res4 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(17),
            null,
            null,
            false);
    assertTrue(res3.clashesWith(res4));
  }

  @Test
  void reservationClashOnlyWithOverlaps() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.MIN.withHour(15),
            LocalDateTime.MIN.withHour(16),
            null,
            null,
            false);
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.MIN.withHour(16),
            LocalDateTime.MIN.withHour(17),
            null,
            null,
            false);
    assertFalse(res1.clashesWith(res2));

    Reservation res3 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.MIN.withHour(15),
            LocalDateTime.MIN.withHour(16),
            null,
            null,
            false);
    Reservation res4 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.MIN.withHour(14),
            LocalDateTime.MIN.withHour(15),
            null,
            null,
            false);
    assertFalse(res3.clashesWith(res4));
  }

  @Test
  void reservationClashWhenIdIsNull() {
    Reservation res1 =
        new Reservation(
            null,
            "deskId1",
            "username1",
            LocalDateTime.MIN.withHour(14),
            LocalDateTime.MIN.withHour(16),
            null,
            null,
            false);
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.MIN.withHour(15),
            LocalDateTime.MIN.withHour(17),
            null,
            null,
            false);
    assertTrue(res1.clashesWith(res2));

    Reservation res3 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.MIN.withHour(14),
            LocalDateTime.MIN.withHour(16),
            null,
            null,
            false);
    Reservation res4 =
        new Reservation(
            null,
            "deskId1",
            "username2",
            LocalDateTime.MIN.withHour(15),
            LocalDateTime.MIN.withHour(15),
            null,
            null,
            false);
    assertTrue(res3.clashesWith(res4));

    Reservation res5 =
        new Reservation(
            null,
            "deskId1",
            "username1",
            LocalDateTime.now().withHour(14),
            LocalDateTime.now().withHour(16),
            null,
            null,
            false);
    Reservation res6 =
        new Reservation(
            null,
            "deskId1",
            "username2",
            LocalDateTime.now().withHour(15),
            LocalDateTime.now().withHour(15),
            null,
            null,
            false);
    assertTrue(res5.clashesWith(res6));
  }

  @Test
  void getStartReturnsRealStartIfSet() {
    LocalDateTime start = LocalDateTime.now().withHour(10);
    LocalDateTime realStart = LocalDateTime.now().withHour(11);
    Reservation testReservation =
        new Reservation(
            "id",
            "deskId",
            "username",
            start,
            LocalDateTime.now().withHour(12),
            realStart,
            null,
            false);
    assertEquals(realStart, testReservation.getStart());
  }

  @Test
  void getEndReturnsRealEndIfSet() {
    LocalDateTime end = LocalDateTime.now().withHour(12);
    LocalDateTime realEnd = LocalDateTime.now().withHour(13);
    Reservation testReservation =
        new Reservation(
            "id",
            "deskId",
            "username",
            LocalDateTime.now().withHour(10),
            end,
            null,
            realEnd,
            false);
    assertEquals(realEnd, testReservation.getEnd());
  }

  @Test
  void getStartReturnsStartIfRealStartNotSet() {
    LocalDateTime start = LocalDateTime.now().withHour(10);
    Reservation testReservation =
        new Reservation(
            "id", "deskId", "username", start, LocalDateTime.now().withHour(12), null, null, false);
    assertEquals(start, testReservation.getStart());
  }

  @Test
  void getEndReturnsEndIfRealEndNotSet() {
    LocalDateTime end = LocalDateTime.now().withHour(12);
    Reservation testReservation =
        new Reservation(
            "id", "deskId", "username", LocalDateTime.now().withHour(10), end, null, null, false);
    assertEquals(end, testReservation.getEnd());
  }
}
