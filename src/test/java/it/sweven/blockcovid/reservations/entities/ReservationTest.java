package it.sweven.blockcovid.reservations.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReservationTest {
  @Test
  void persistenceConstructionShouldSetTheId() {
    Reservation fakeRes =
        new Reservation("id1", "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX);
    assertEquals("id1", fakeRes.getId());
  }

  @Test
  void comparisonIsDoneOnTheStartingTIme() {
    Reservation fakeRes1 =
        new Reservation(
            "id1", "deskId", "username", LocalDateTime.MIN.plusMinutes(5), LocalDateTime.MAX);
    Reservation fakeRes2 =
        new Reservation(
            "id1", "deskId", "username", LocalDateTime.MIN.plusMinutes(10), LocalDateTime.MAX);
    Reservation fakeRes3 =
        new Reservation("id1", "deskId", "username", LocalDateTime.MIN, LocalDateTime.MAX);
    Reservation fakeRes4 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.MIN.plusMinutes(10),
            LocalDateTime.MAX.minusMinutes(10));
    assertEquals(-1, fakeRes1.compareTo(fakeRes2));
    assertEquals(1, fakeRes1.compareTo(fakeRes3));
    assertEquals(0, fakeRes2.compareTo(fakeRes4));
  }

  @Test
  void reservationNeverClashesWithHerself() {
    Reservation res1 =
        new Reservation(
            "id1", "deskId", "username", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
    Reservation res2 =
        new Reservation(
            "id1",
            "deskId",
            "username",
            LocalDateTime.now().minusMinutes(3),
            LocalDateTime.now().plusMinutes(10));
    assertFalse(res1.clashesWith(res2));
  }

  @Test
  void reservationClashesOnlyIfSameDesk() {
    Reservation res1 =
        new Reservation(
            "id1", "deskId1", "username", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId2",
            "username",
            LocalDateTime.now().minusMinutes(3),
            LocalDateTime.now().plusMinutes(10));
    assertFalse(res1.clashesWith(res2));
  }

  @Test
  void reservationClashesIfTimeIntervalsOverlap() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(10));
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.now().minusMinutes(3),
            LocalDateTime.now().plusMinutes(5));
    assertTrue(res1.clashesWith(res2));

    Reservation res3 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(10));
    Reservation res4 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.now().plusMinutes(3),
            LocalDateTime.now().plusMinutes(5));
    assertTrue(res3.clashesWith(res4));
  }

  @Test
  void reservationClashOnlyWithOverlaps() {
    Reservation res1 =
        new Reservation(
            "id1",
            "deskId1",
            "username1",
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(10));
    Reservation res2 =
        new Reservation(
            "id2",
            "deskId1",
            "username2",
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusMinutes(20));
    assertFalse(res1.clashesWith(res2));

    LocalDateTime now = LocalDateTime.now();
    Reservation res3 = new Reservation("id1", "deskId1", "username1", now, now.plusMinutes(10));
    Reservation res4 = new Reservation("id2", "deskId1", "username2", now.minusMinutes(10), now);
    assertFalse(res3.clashesWith(res4));
  }
}
