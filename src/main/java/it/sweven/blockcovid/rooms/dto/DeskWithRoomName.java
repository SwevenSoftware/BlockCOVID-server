package it.sweven.blockcovid.rooms.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DeskWithRoomName {
  private final String roomName;
  private final Integer x, y;
}
