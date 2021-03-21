package it.sweven.blockcovid.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.entities.room.Room;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class RoomAssemblerTest {
  private RoomAssembler assembler;

  @BeforeEach
  void init() {
    assembler = new RoomAssembler();
  }

  @Test
  void toModel() {
    Room fakeRoom = mock(Room.class);
    assertEquals(fakeRoom, assembler.toModel(fakeRoom).getContent());
  }

  @Test
  void toCollectionModel() {
    List<Room> rooms = List.of(mock(Room.class), mock(Room.class), mock(Room.class));
    assertEquals(
        rooms,
        assembler.toCollectionModel(rooms).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }
}
