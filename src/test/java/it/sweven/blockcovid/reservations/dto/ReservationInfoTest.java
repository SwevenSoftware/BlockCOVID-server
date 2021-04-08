package it.sweven.blockcovid.reservations.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReservationInfoTest {

  @Test
  void isValid() {
    ReservationInfo validInfo = new ReservationInfo("desk", LocalDateTime.MIN, LocalDateTime.MAX);
    assertTrue(validInfo.isValid());
  }

  @Test
  void nullDeskIdIsNotValid() {
    ReservationInfo invalidInfo = new ReservationInfo(null, LocalDateTime.MIN, LocalDateTime.MAX);
    assertFalse(invalidInfo.isValid());
  }

  @Test
  void nullStartingTimeIsNotValid() {
    ReservationInfo invalidInfo = new ReservationInfo("desk", null, LocalDateTime.MAX);
    assertFalse(invalidInfo.isValid());
  }

  @Test
  void nullEndingTimeIsNotValid() {
    ReservationInfo invalidInfo = new ReservationInfo("desk", LocalDateTime.MIN, null);
    assertFalse(invalidInfo.isValid());
  }

  @Test
  void startingTimeShouldBeBeforeEndingTime() {
    ReservationInfo invalidInfo = new ReservationInfo("desk", LocalDateTime.MAX, LocalDateTime.MIN);
    assertFalse(invalidInfo.isValid());
  }
}
