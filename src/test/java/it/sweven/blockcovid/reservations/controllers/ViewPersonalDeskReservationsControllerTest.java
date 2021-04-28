package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;

class ViewPersonalDeskReservationsControllerTest {
  private ViewPersonalDeskReservationsController controller;
  private ReservationWithRoom fakeRes1, fakeRes2;
  private final String deskId = "desk 1";

  @BeforeEach
  void setUp() {
    ReservationService service = mock(ReservationService.class);
    ReservationWithRoomAssembler assembler = mock(ReservationWithRoomAssembler.class);
    doAnswer(invocationOnMock -> CollectionModel.of(invocationOnMock.getArgument(0)))
        .when(assembler)
        .toCollectionModel(any());
    controller = new ViewPersonalDeskReservationsController(service, assembler);

    fakeRes1 = mock(ReservationWithRoom.class);
    when(fakeRes1.getDeskId()).thenReturn("desk 2");
    fakeRes2 = mock(ReservationWithRoom.class);
    when(fakeRes2.getDeskId()).thenReturn(deskId);

    List<ReservationWithRoom> fakeList = List.of(fakeRes1, fakeRes2);
    when(service.findByUsernameAndStart(any(), any())).thenReturn(fakeList);
  }

  @Test
  void happyPath() {
    assertTrue(
        controller
            .viewAll(mock(User.class), LocalDateTime.now(), deskId)
            .getContent()
            .contains(fakeRes2));
    assertFalse(
        controller
            .viewAll(mock(User.class), LocalDateTime.now(), deskId)
            .getContent()
            .contains(fakeRes1));
  }
}
