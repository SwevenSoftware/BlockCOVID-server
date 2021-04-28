package it.sweven.blockcovid.reservations.entities;

import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.springframework.stereotype.Component;

@Component
public class ReservationBuilder {
  private String id, username, deskId;
  private LocalDateTime start, end, realStart, realEnd;
  private Boolean deskCleaned = false;

  public ReservationBuilder id(String id) {
    this.id = id;
    return this;
  }

  public ReservationBuilder username(String username) {
    this.username = username;
    return this;
  }

  public ReservationBuilder deskId(String deskId) {
    this.deskId = deskId;
    return this;
  }

  public ReservationBuilder start(LocalDateTime start) {
    this.start = start;
    return this;
  }

  public ReservationBuilder end(LocalDateTime end) {
    this.end = end;
    return this;
  }

  public ReservationBuilder realStart(LocalDateTime realStart) {
    this.realStart = realStart;
    return this;
  }

  public ReservationBuilder realEnd(LocalDateTime realEnd) {
    this.realEnd = realEnd;
    return this;
  }

  public ReservationBuilder deskCleaned(Boolean deskCleaned) {
    this.deskCleaned = deskCleaned;
    return this;
  }

  public ReservationBuilder from(ReservationWithRoom reservation) {
    return id(reservation.getId())
        .username(reservation.getUsername())
        .deskId(reservation.getDeskId())
        .start(reservation.getStart())
        .end(reservation.getEnd())
        .realStart(reservation.getUsageStart())
        .realEnd(reservation.getUsageEnd())
        .deskCleaned(reservation.getDeskCleaned() != null ? reservation.getDeskCleaned() : false);
  }

  public Reservation build() throws BadAttributeValueExpException {
    if (deskId == null || username == null || start == null || end == null)
      throw new BadAttributeValueExpException("fields not specified");
    if (start.isAfter(end)) throw new BadAttributeValueExpException("start may not be after end");
    if (realStart == null && realEnd != null)
      throw new BadAttributeValueExpException("usage can't be ended without first be started");
    if (realEnd == null && deskCleaned)
      throw new BadAttributeValueExpException("desk can't be cleaned without first ending usage");
    return new Reservation(id, deskId, username, start, end, realStart, realEnd, deskCleaned);
  }
}
