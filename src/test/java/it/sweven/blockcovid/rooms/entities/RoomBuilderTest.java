package it.sweven.blockcovid.rooms.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.Test;

class RoomBuilderTest {
  @Test
  void rightCreateRoom() throws BadAttributeValueExpException {
    RoomBuilder builder = new RoomBuilder();
    Room expected =
        new Room(
            "name",
            false,
            LocalTime.MIDNIGHT,
            LocalTime.MIDNIGHT,
            Set.of(DayOfWeek.MONDAY),
            10,
            10,
            Status.CLEAN);
    Room generated =
        builder
            .name("name")
            .closed(false)
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .roomStatus(Status.CLEAN)
            .width(10)
            .height(10)
            .build();
    assertEquals(expected, generated);
  }

  @Test
  void noNameThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .closed(true)
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .openingTime(LocalTime.MIDNIGHT)
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .width(10)
              .height(10)
              .build();
        });
  }

  @Test
  void noOpeningDaysThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .openingTime(LocalTime.MIDNIGHT)
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .width(10)
              .height(10)
              .build();
        });
  }

  @Test
  void noOpeningTimeThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .width(10)
              .height(10)
              .build();
        });
  }

  @Test
  void noClosingTimeThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .openingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .width(10)
              .height(10)
              .build();
        });
  }

  @Test
  void noWidthThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .openingTime(LocalTime.MIDNIGHT)
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .height(10)
              .build();
        });
  }

  @Test
  void noHeightThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .openingTime(LocalTime.MIDNIGHT)
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .width(10)
              .build();
        });
  }

  @Test
  void noClosedSetsItAsFalse() throws BadAttributeValueExpException {
    Room created =
        new RoomBuilder()
            .name("name")
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .roomStatus(Status.CLEAN)
            .width(10)
            .height(10)
            .build();
    assertFalse(created.isClosed());
  }

  @Test
  void noStatusSetsItAsClean() throws BadAttributeValueExpException {
    Room created =
        new RoomBuilder()
            .name("name")
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .openingTime(LocalTime.MIDNIGHT)
            .closingTime(LocalTime.MIDNIGHT)
            .roomStatus(Status.CLEAN)
            .width(10)
            .height(10)
            .build();
    assertEquals(Status.CLEAN, created.getRoomStatus());
  }

  @Test
  void nullRoomStatusThrowsException() {
    assertThrows(
        BadAttributeValueExpException.class,
        () -> {
          new RoomBuilder()
              .name("name")
              .roomStatus(null)
              .openingDays(Set.of(DayOfWeek.MONDAY))
              .openingTime(LocalTime.MIDNIGHT)
              .closingTime(LocalTime.MIDNIGHT)
              .roomStatus(Status.CLEAN)
              .height(10)
              .build();
        });
  }
}
