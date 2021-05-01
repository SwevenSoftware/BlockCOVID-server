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
  private LocalDateTime start, end, realStart, realEnd;
  private Boolean deskCleaned;

  @PersistenceConstructor
  Reservation(
      String id,
      String deskId,
      String username,
      LocalDateTime start,
      LocalDateTime end,
      LocalDateTime realStart,
      LocalDateTime realEnd,
      Boolean deskCleaned) {
    this.id = id;
    this.deskId = deskId;
    this.username = username;
    this.start = start;
    this.end = end;
    this.realStart = realStart;
    this.realEnd = realEnd;
    this.deskCleaned = deskCleaned;
  }

  @Override
  public int compareTo(Reservation other) {
    return start.compareTo(other.getStart());
  }

  public boolean intervalInsideReservation(LocalDateTime start, LocalDateTime end) {
    LocalDateTime minStart = minBetween(getStart(), getRealStart()),
        minEnd = minBetween(getEnd(), getRealEnd());
    return (!start.isBefore(minStart) && minEnd.isAfter(start))
        || (start.isBefore(minStart) && end.isAfter(minStart));
  }

  public boolean clashesWith(Reservation other) {
    return (other.getId() == null || getId() == null || !other.getId().equals(this.id))
        && deskId.equals(other.getDeskId())
        && intervalInsideReservation(
            minBetween(other.getStart(), other.getRealStart()),
            minBetween(other.getEnd(), other.getRealEnd()));
  }

  private LocalDateTime minBetween(LocalDateTime timestamp1, LocalDateTime timestamp2) {
    if (timestamp1 == null) return timestamp2;
    if (timestamp2 == null) return timestamp1;
    if (timestamp1.isBefore(timestamp2)) return timestamp1;
    return timestamp2;
  }

  public Boolean isEnded() {
    return LocalDateTime.now().isAfter(end) || realEnd != null;
  }
}
