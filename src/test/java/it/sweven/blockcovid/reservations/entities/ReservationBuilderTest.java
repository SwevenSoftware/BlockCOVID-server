package it.sweven.blockcovid.reservations.entities;

import static org.junit.jupiter.api.Assertions.*;

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
}
