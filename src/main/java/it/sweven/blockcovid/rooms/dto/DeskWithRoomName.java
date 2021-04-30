package it.sweven.blockcovid.rooms.dto;

import it.sweven.blockcovid.rooms.entities.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DeskWithRoomName {
  private final String roomName, deskId;
  private final Integer x, y;
  private final Status status;
}
