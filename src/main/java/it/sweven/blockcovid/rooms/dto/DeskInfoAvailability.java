package it.sweven.blockcovid.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DeskInfoAvailability {
  private final String deskId;
  private final Integer x, y;
  private final Boolean available;
}
