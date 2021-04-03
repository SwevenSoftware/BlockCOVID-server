package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

class ListRoomsControllerTest {

  private RoomService roomService;
  private DeskService deskService;
  private RoomWithDesksAssembler assembler;
  private ListRoomsController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    deskService = mock(DeskService.class);
    assembler = mock(RoomWithDesksAssembler.class);
    controller = new ListRoomsController(roomService, deskService, assembler);
  }

  @Test
  void listRooms() {
    when(roomService.getAllRooms()).thenReturn(List.of(mock(Room.class), mock(Room.class)));
    List<RoomWithDesks> roomsWithDesks =
        List.of(mock(RoomWithDesks.class), mock(RoomWithDesks.class));
    CollectionModel<EntityModel<RoomWithDesks>> expectedCollection =
        CollectionModel.of(
            roomsWithDesks.stream().map(EntityModel::of).collect(Collectors.toList()));
    when(assembler.toCollectionModel(any())).thenReturn(expectedCollection);
    assertEquals(expectedCollection, controller.listRooms(mock(User.class)));
  }
}
