package it.sweven.blockcovid.reservations.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationInfo implements Dto {
  private final String deskId;
  private final LocalDateTime start;
  private final LocalDateTime end;

  @Override
  public boolean isValid() {
    return deskId != null && start != null && end != null && start.isBefore(end);
  }
}
