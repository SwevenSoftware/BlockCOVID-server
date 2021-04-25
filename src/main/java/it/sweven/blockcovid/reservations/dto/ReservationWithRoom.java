package it.sweven.blockcovid.reservations.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReservationWithRoom {
  private final String id, deskId, room, username;
  private final LocalDateTime start, end, usageStart, usageEnd;
  private final Boolean deskCleaned;
}
