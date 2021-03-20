package it.sweven.blockcovid.entities.room;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomTest {

  private Room room;

  @BeforeEach
  void setUp() {
    room = new Room();
  }

  @Test
  void setOpeningTime_nullClosingTime_correctInput() {
    LocalTime expectedTime = LocalTime.of(14, 20);
    room.setOpeningTime(expectedTime);
    assertEquals(expectedTime, room.getOpeningTime());
  }

  @Test
  void setOpeningTime_openingTimeAfterClosingTime_throwsIllegalArgumentException() {
    room = new Room("room", null, LocalTime.of(13, 0), Set.of(DayOfWeek.MONDAY), 100, 100);
    assertThrows(IllegalArgumentException.class, () -> room.setOpeningTime(LocalTime.of(16, 50)));
  }

  @Test
  void setOpeningTime_nonNullClosingTime_openingTimeBeforeClosingTime() {
    LocalTime expectedTime = LocalTime.of(11, 20);
    room = new Room("room", null, LocalTime.of(13, 0), Set.of(DayOfWeek.MONDAY), 100, 100);
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
  void setClosingTime_closingTimeBeforeOpeningTime_throwsIllegalArgumentException() {
    room = new Room("room", LocalTime.of(11, 30), null, Set.of(DayOfWeek.MONDAY), 100, 100);
    assertThrows(IllegalArgumentException.class, () -> room.setClosingTime(LocalTime.of(10, 40)));
  }

  @Test
  void setClosingTime_closingTimeAfterOpeningTime() {
    LocalTime expectedTime = LocalTime.of(18, 30);
    room =
        new Room(
            "idRoom", "room", false, LocalTime.of(14, 0), null, Set.of(DayOfWeek.MONDAY), 100, 100);
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
}
