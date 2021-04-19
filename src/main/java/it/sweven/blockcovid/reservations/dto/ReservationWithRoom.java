package it.sweven.blockcovid.reservations.dto;

import it.sweven.blockcovid.reservations.entities.Reservation;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationWithRoom {
  private final String id, deskId, room, username;
  private final LocalDateTime start, end;

  public Reservation toReservation() {
    return new Reservation(id, deskId, username, start, end);
  }
}
