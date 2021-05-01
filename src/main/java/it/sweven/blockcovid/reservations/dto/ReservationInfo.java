package it.sweven.blockcovid.reservations.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReservationInfo {
  private final String deskId;
  private LocalDateTime start;
  private final LocalDateTime end;
}
