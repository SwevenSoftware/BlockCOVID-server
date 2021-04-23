package it.sweven.blockcovid.rooms.dto;

import it.sweven.blockcovid.rooms.entities.Room;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RoomWithDesks {
  private final Room room;
  private final List<DeskInfoAvailability> desks;
}
