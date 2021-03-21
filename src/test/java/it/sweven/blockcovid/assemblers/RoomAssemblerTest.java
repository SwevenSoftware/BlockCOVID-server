package it.sweven.blockcovid.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.entities.room.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
