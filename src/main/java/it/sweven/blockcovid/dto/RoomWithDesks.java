package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.room.Room;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RoomWithDesks {
  private final Room room;
  private final List<DeskInfo> desks;
}
