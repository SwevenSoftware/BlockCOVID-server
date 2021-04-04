package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class DeleteRoomControllerTest {
  private RoomAssembler roomAssembler;
  private RoomService roomService;
  private User submitter;
  private DeleteRoomController controller;

  @BeforeEach
  void setUp() {
    roomAssembler = mock(RoomAssembler.class);
    when(roomAssembler.setAuthorities(anySet())).thenReturn(roomAssembler);
    roomService = mock(RoomService.class);
    submitter = mock(User.class);
    when(submitter.getAuthorities()).thenReturn(Set.of(Authority.ADMIN));
    controller = new DeleteRoomController(roomService, roomAssembler);
  }

  @Test
  void validDeletion() {
    Room fakeDeleted = mock(Room.class);
    when(roomService.deleteRoomByName(anyString())).thenReturn(fakeDeleted);
    when(roomService.getByName(anyString())).thenReturn(fakeDeleted);
    when(roomAssembler.toModel(any())).thenReturn(EntityModel.of(fakeDeleted));
    assertEquals(fakeDeleted, controller.delete(submitter, "room").getContent());
  }

  @Test
  void roomNotFound_throwsResponseStatusException() {
    when(roomService.deleteRoomByName(anyString())).thenThrow(new RoomNotFoundException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.delete(submitter, "room"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
