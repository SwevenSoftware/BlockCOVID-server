package it.sweven.blockcovid.rooms.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomTest {

  private Room room;

  @BeforeEach
  void setUp() {
    room = new Room();
  }

  @Test
  void persistenceConstructor() {
    Room test =
        new Room(
            "id",
            "name",
            false,
            LocalTime.MIDNIGHT,
            LocalTime.MIDNIGHT,
            Set.of(DayOfWeek.MONDAY),
            10,
            10,
            Status.CLEAN);
  }

  @Test
  void setOpeningTime_nullClosingTime_correctInput() {
    LocalTime expectedTime = LocalTime.of(14, 20);
    room.setOpeningTime(expectedTime);
    assertEquals(expectedTime, room.getOpeningTime());
  }

  @Test
  void setOpeningTime_openingTimeAfterClosingTime_throwsIllegalArgumentException()
      throws BadAttributeValueExpException {
    room =
        new RoomBuilder()
            .name("room")
            .closingTime(LocalTime.of(15, 0))
            .openingTime(LocalTime.of(13, 0))
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(100)
            .height(100)
            .build();
    assertThrows(IllegalArgumentException.class, () -> room.setOpeningTime(LocalTime.of(16, 50)));
  }

  @Test
  void setOpeningTime_nonNullClosingTime_openingTimeBeforeClosingTime()
      throws BadAttributeValueExpException {
    LocalTime expectedTime = LocalTime.of(11, 20);
    room =
        new RoomBuilder()
            .name("room")
            .closingTime(LocalTime.of(15, 0))
            .openingTime(LocalTime.of(13, 0))
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(100)
            .height(100)
            .build();
    room.setOpeningTime(expectedTime);
    assertEquals(expectedTime, room.getOpeningTime());
  }

  @Test
  void setOpeningTime_nullInput() {
    room.setOpeningTime(null);
  }

  @Test
  void setClosingTime_nullOpeningTime_correctInput() {
    LocalTime expectedTime = LocalTime.of(14, 20);
    room.setClosingTime(expectedTime);
    assertEquals(expectedTime, room.getClosingTime());
  }

  @Test
  void setClosingTime_closingTimeBeforeOpeningTime_throwsIllegalArgumentException()
      throws BadAttributeValueExpException {
    room =
        new RoomBuilder()
            .name("room")
            .closingTime(LocalTime.of(15, 0))
            .openingTime(LocalTime.of(13, 0))
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(100)
            .height(100)
            .build();
    assertThrows(IllegalArgumentException.class, () -> room.setClosingTime(LocalTime.of(10, 40)));
  }

  @Test
  void setClosingTime_closingTimeAfterOpeningTime() throws BadAttributeValueExpException {
    LocalTime expectedTime = LocalTime.of(18, 30);
    room =
        new RoomBuilder()
            .name("room")
            .openingTime(LocalTime.of(14, 0))
            .closingTime(LocalTime.of(19, 0))
            .openingDays(Set.of(DayOfWeek.MONDAY))
            .width(100)
            .height(100)
            .build();
    room.setClosingTime(expectedTime);
    assertEquals(expectedTime, room.getClosingTime());
  }

  @Test
  void setClosingTime_nullInput() {
    room.setClosingTime(null);
  }

  @Test
  void setOpeningDays_emptySet_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> room.setOpeningDays(Collections.emptySet()));
  }

  @Test
  void setWidth_domain() {
    room.setWidth(10);
    assertThrows(IllegalArgumentException.class, () -> room.setWidth(0));
    assertThrows(IllegalArgumentException.class, () -> room.setWidth(-10));
  }

  @Test
  void setHeight_domain() {
    room.setHeight(10);
    assertThrows(IllegalArgumentException.class, () -> room.setHeight(0));
    assertThrows(IllegalArgumentException.class, () -> room.setHeight(-10));
  }

  @Test
  void getRoomStatus_nullStatus() {
    room =
        new Room(
            "idRoom",
            "room",
            false,
            LocalTime.of(14, 0),
            null,
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            null);
    assertEquals(Status.DIRTY, room.getRoomStatus());
  }

  @Test
  void getRoomStatus_nonNullStatus() {
    room =
        new Room(
            "idRoom",
            "room",
            false,
            LocalTime.of(14, 0),
            null,
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    assertEquals(Status.CLEAN, room.getRoomStatus());
  }

  @Test
  void isRoomOpenCheckIfClosed() {
    room =
        new Room(
            "idRoom",
            "room",
            true,
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    assertFalse(room.isRoomOpen(LocalDateTime.now()));
  }

  @Test
  void isRoomOpenChecksOpeningDays() {
    room =
        new Room(
            "idRoom",
            "room",
            true,
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    /* 2021-04-20T15.30 has valid time but it's tuesday */
    assertFalse(room.isRoomOpen(LocalDateTime.of(2021, 4, 20, 15, 30)));
  }

  @Test
  void isRoomOpenChecksOpeningTime() {
    room =
        new Room(
            "idRoom",
            "room",
            true,
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    /* 2021-04-20T15.30 has valid time but it's tuesday */
    assertFalse(room.isRoomOpen(LocalDateTime.of(2021, 4, 19, 12, 30)));
  }

  @Test
  void isRoomOpenChecksClosingTime() {
    room =
        new Room(
            "idRoom",
            "room",
            true,
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    /* 2021-04-20T15.30 has valid time but it's tuesday */
    assertFalse(room.isRoomOpen(LocalDateTime.of(2021, 4, 19, 19, 30)));
  }

  @Test
  void isRoomOpenRightAnswer() {
    room =
        new Room(
            "idRoom",
            "room",
            true,
            LocalTime.of(14, 0),
            LocalTime.of(18, 0),
            Set.of(DayOfWeek.MONDAY),
            100,
            100,
            Status.CLEAN);
    /* 2021-04-20T15.30 has valid time but it's tuesday */
    assertFalse(room.isRoomOpen(LocalDateTime.of(2021, 4, 19, 15, 30)));
  }
}
