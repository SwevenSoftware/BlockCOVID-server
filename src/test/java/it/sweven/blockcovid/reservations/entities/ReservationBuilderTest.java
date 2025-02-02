package it.sweven.blockcovid.reservations.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationBuilderTest {

  private ReservationBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new ReservationBuilder();
  }

  @Test
  void build_nullDeskId_throwsBadAttributeValueExpException() {
    builder.id("id").username("username").start(LocalDateTime.MIN).end(LocalDateTime.MAX);
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_nullUsername_throwsBadAttributeValueExpException() {
    builder.id("id").deskId("deskId").start(LocalDateTime.MIN).end(LocalDateTime.MAX);
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_nullStart_throwsBadAttributeValueExpException() {
    builder.id("id").deskId("deskId").username("username").end(LocalDateTime.MAX);
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_nullEnd_throwsBadAttributeValueExpException() {
    builder.id("id").deskId("deskId").username("username").start(LocalDateTime.MIN);
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_startAfterEnd_throwsBadAttributeValueExpException() {
    builder
        .id("id")
        .deskId("deskId")
        .username("username")
        .start(LocalDateTime.MIN.withHour(15))
        .end(LocalDateTime.MIN.withHour(13));
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_realEndWithoutRealStart_throwsBadAttributeValueExpException() {
    builder
        .id("id")
        .deskId("deskId")
        .username("username")
        .start(LocalDateTime.MIN.withHour(10))
        .end(LocalDateTime.MIN.withHour(16))
        .realEnd(LocalDateTime.MIN.withHour(14));
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_deskCleanedBeforeRealEnd_throwsBadAttributeValueExpException() {
    builder
        .id("id")
        .deskId("deskId")
        .username("username")
        .start(LocalDateTime.MIN.withHour(10))
        .end(LocalDateTime.MIN.withHour(16))
        .deskCleaned(true);
    assertThrows(BadAttributeValueExpException.class, () -> builder.build());
  }

  @Test
  void build_validWithId() throws BadAttributeValueExpException {
    Reservation reservation =
        builder
            .id("id")
            .deskId("deskId")
            .username("username")
            .start(LocalDateTime.MIN.withHour(13))
            .end(LocalDateTime.MIN.withHour(15))
            .build();
    assertEquals(reservation.getId(), "id");
    assertEquals(reservation.getDeskId(), "deskId");
    assertEquals(reservation.getUsername(), "username");
    assertEquals(reservation.getStart(), LocalDateTime.MIN.withHour(13));
    assertEquals(reservation.getEnd(), LocalDateTime.MIN.withHour(15));
  }

  @Test
  void build_validWithoutId() throws BadAttributeValueExpException {
    Reservation reservation =
        builder
            .deskId("deskId")
            .username("username")
            .start(LocalDateTime.MIN.withHour(13))
            .end(LocalDateTime.MIN.withHour(15))
            .build();
    assertNull(reservation.getId());
    assertEquals(reservation.getDeskId(), "deskId");
    assertEquals(reservation.getUsername(), "username");
    assertEquals(reservation.getStart(), LocalDateTime.MIN.withHour(13));
    assertEquals(reservation.getEnd(), LocalDateTime.MIN.withHour(15));
  }

  @Test
  void build_validWithRealStart() throws BadAttributeValueExpException {
    Reservation reservation =
        builder
            .deskId("deskId")
            .username("username")
            .start(LocalDateTime.MIN.withHour(13))
            .end(LocalDateTime.MIN.withHour(15))
            .realStart(LocalDateTime.MIN.withHour(13))
            .build();
    assertNull(reservation.getId());
    assertEquals(reservation.getDeskId(), "deskId");
    assertEquals(reservation.getUsername(), "username");
    assertEquals(reservation.getStart(), LocalDateTime.MIN.withHour(13));
    assertEquals(reservation.getEnd(), LocalDateTime.MIN.withHour(15));
    assertEquals(reservation.getRealStart(), LocalDateTime.MIN.withHour(13));
  }

  @Test
  void from_getDeskCleanedNull() throws BadAttributeValueExpException {
    LocalDateTime now = LocalDateTime.now();
    ReservationWithRoom reservationWithRoom = mock(ReservationWithRoom.class);
    when(reservationWithRoom.getId()).thenReturn("id1");
    when(reservationWithRoom.getUsername()).thenReturn("user");
    when(reservationWithRoom.getDeskId()).thenReturn("deskId1");
    when(reservationWithRoom.getStart()).thenReturn(now);
    when(reservationWithRoom.getEnd()).thenReturn(now.plusMinutes(10));
    when(reservationWithRoom.getUsageStart()).thenReturn(now.minusMinutes(10));
    when(reservationWithRoom.getUsageEnd()).thenReturn(null);
    when(reservationWithRoom.getDeskCleaned()).thenReturn(null);

    Reservation fakeRes = new ReservationBuilder().from(reservationWithRoom).build();

    assertEquals(reservationWithRoom.getId(), fakeRes.getId());
    assertEquals(reservationWithRoom.getUsername(), fakeRes.getUsername());
    assertEquals(reservationWithRoom.getDeskId(), fakeRes.getDeskId());
    assertEquals(reservationWithRoom.getStart(), fakeRes.getStart());
    assertEquals(reservationWithRoom.getEnd(), fakeRes.getEnd());
    assertEquals(reservationWithRoom.getEnd(), fakeRes.getEnd());
    assertEquals(reservationWithRoom.getUsageEnd(), fakeRes.getRealEnd());
    assertEquals(reservationWithRoom.getUsageStart(), fakeRes.getRealStart());
    assertFalse(fakeRes.getDeskCleaned());
  }

  @Test
  void from_deskCleanedWithoutEnd_throwsBadAttributeValueExpException()
      throws BadAttributeValueExpException {
    LocalDateTime now = LocalDateTime.now();
    ReservationWithRoom reservationWithRoom = mock(ReservationWithRoom.class);
    when(reservationWithRoom.getId()).thenReturn("id1");
    when(reservationWithRoom.getUsername()).thenReturn("user");
    when(reservationWithRoom.getDeskId()).thenReturn("deskId1");
    when(reservationWithRoom.getStart()).thenReturn(now);
    when(reservationWithRoom.getEnd()).thenReturn(now.plusMinutes(10));
    when(reservationWithRoom.getUsageStart()).thenReturn(now.minusMinutes(10));
    when(reservationWithRoom.getUsageEnd()).thenReturn(now.plusMinutes(11));
    when(reservationWithRoom.getDeskCleaned()).thenReturn(true);

    Reservation fakeRes = new ReservationBuilder().from(reservationWithRoom).build();

    assertEquals(reservationWithRoom.getId(), fakeRes.getId());
    assertEquals(reservationWithRoom.getUsername(), fakeRes.getUsername());
    assertEquals(reservationWithRoom.getDeskId(), fakeRes.getDeskId());
    assertEquals(reservationWithRoom.getStart(), fakeRes.getStart());
    assertEquals(reservationWithRoom.getEnd(), fakeRes.getEnd());
    assertEquals(reservationWithRoom.getEnd(), fakeRes.getEnd());
    assertEquals(reservationWithRoom.getUsageEnd(), fakeRes.getRealEnd());
    assertEquals(reservationWithRoom.getUsageStart(), fakeRes.getRealStart());
    assertTrue(fakeRes.getDeskCleaned());
  }
}
