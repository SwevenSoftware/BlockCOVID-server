package it.sweven.blockcovid.rooms.assemblers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.users.entities.Authority;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class DeskAssemblerTest {
  private DeskAssembler assembler;

  @BeforeEach
  void init() {
    assembler = new DeskAssembler();
  }

  @Test
  void toModel() {
    DeskWithRoomName expectedDesk = mock(DeskWithRoomName.class);
    assertEquals(expectedDesk, assembler.toModel(expectedDesk).getContent());
  }

  @Test
  void adminToModel() {
    DeskWithRoomName expectedDesk = mock(DeskWithRoomName.class);
    assertEquals(
        expectedDesk,
        assembler.setAuthorities(Set.of(Authority.ADMIN)).toModel(expectedDesk).getContent());
  }

  @Test
  void toCollectionModel() {
    List<DeskWithRoomName> desks =
        List.of(
            mock(DeskWithRoomName.class),
            mock(DeskWithRoomName.class),
            mock(DeskWithRoomName.class));
    assertEquals(
        desks,
        assembler.toCollectionModel(desks).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }
}
