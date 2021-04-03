package it.sweven.blockcovid.rooms.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Room;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class RoomWithDesksAssemblerTest {
  private RoomWithDesksAssembler assembler;

  @BeforeEach
  void init() {
    assembler = new RoomWithDesksAssembler();
  }

  @Test
  void toModel() {
    RoomWithDesks entity = mock(RoomWithDesks.class);
    Room associatedRoom = mock(Room.class);
    when(associatedRoom.getName()).thenReturn("roomName");
    when(entity.getRoom()).thenReturn(associatedRoom);
    assertEquals(entity, assembler.toModel(entity).getContent());
  }

  @Test
  void toCollectionModel() {
    Room room1 = mock(Room.class), room2 = mock(Room.class);
    when(room1.getName()).thenReturn("room1Name");
    when(room2.getName()).thenReturn("room2Name");
    List<RoomWithDesks> entities =
        List.of(
            new RoomWithDesks(room1, List.of(mock(DeskInfo.class))),
            new RoomWithDesks(room2, List.of(mock(DeskInfo.class), mock(DeskInfo.class))));
    assertEquals(
        entities,
        assembler.toCollectionModel(entities).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }
}
