package it.sweven.blockcovid.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DeskWithRoomName {
  private final Integer Id;
  private final String roomName;
  private final Integer x, y;
}
