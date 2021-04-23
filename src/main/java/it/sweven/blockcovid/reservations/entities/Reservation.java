package it.sweven.blockcovid.reservations.entities;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

@Getter
@Setter
public class Reservation implements Comparable<Reservation> {
  @Id
  @Setter(value = AccessLevel.NONE)
  private String id;

  private String username, deskId;
  private LocalDateTime start, end;

  @PersistenceConstructor
  Reservation(String id, String deskId, String username, LocalDateTime start, LocalDateTime end) {
    this.id = id;
    this.deskId = deskId;
    this.username = username;
    this.start = start;
    this.end = end;
  }

  public Reservation(String deskId, String username, LocalDateTime start, LocalDateTime end) {
    this.deskId = deskId;
    this.username = username;
    this.start = start;
    this.end = end;
  }

  @Override
  public int compareTo(Reservation other) {
    return start.compareTo(other.getStart());
  }

  public boolean intervalInsideReservation(LocalDateTime start, LocalDateTime end) {
    return (this.start.isBefore(start) && this.end.isAfter(start))
        || (start.isBefore(this.start) && this.start.isBefore(end));
  }

  public boolean clashesWith(Reservation other) {
    return !other.getId().equals(this.id)
        && deskId.equals(other.getDeskId())
        && intervalInsideReservation(other.start, other.end);
  }
}
