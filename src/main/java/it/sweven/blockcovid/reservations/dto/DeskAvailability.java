package it.sweven.blockcovid.reservations.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeskAvailability {
  private final boolean available;
  private final LocalDateTime nextChange;
}
