package it.sweven.blockcovid.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomInfo {
  private final String name;
  private final LocalTime openingAt;
  private final LocalTime closingAt;
  private final Set<DayOfWeek> openingDays;
  private final Integer width;
  private final Integer height;
}
