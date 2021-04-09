package it.sweven.blockcovid.rooms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ViewDeskStateControllerTest {
  private DeskService deskService;
  private ViewDeskStateController controller;

  @BeforeEach
  void setUp() {
    deskService = mock(DeskService.class);
    controller = new ViewDeskStateController(deskService);
  }

  @Test
  void validRequest() {
    Desk fakeDesk = mock(Desk.class);
    when(fakeDesk.getDeskStatus()).thenReturn(Status.CLEAN);
    when(deskService.getDeskById(any())).thenReturn(fakeDesk);
    EntityModel<Status> returned = controller.deskState(mock(User.class), "id");
    assertEquals(Status.CLEAN, returned.getContent());
    assertTrue(returned.getLinks().hasLink("self"));
  }

  @Test
  void idNotFound() {
    when(deskService.getDeskById(any())).thenThrow(new DeskNotFoundException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> controller.deskState(mock(User.class), "id"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
