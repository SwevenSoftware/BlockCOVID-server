package it.sweven.blockcovid.reservations.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationInfo {
  private final String deskId;
  private final LocalDateTime start;
  private final LocalDateTime end;
}
