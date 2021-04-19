package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

class ViewPersonalReservationsControllerTest {
  private ViewPersonalReservationsController controller;
  private ReservationService service;
  private ReservationWithRoomAssembler assembler;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    assembler = mock(ReservationWithRoomAssembler.class);
    controller = new ViewPersonalReservationsController(service, assembler);
  }

  @Test
  void happyPath() {
    List<ReservationWithRoom> fakeList =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    CollectionModel<EntityModel<ReservationWithRoom>> fakeCollectionModel =
        CollectionModel.of(fakeList.stream().map(EntityModel::of).collect(Collectors.toList()));
    when(service.findByUsernameAndStart(anyString(), any())).thenReturn(fakeList);
    when(assembler.toCollectionModel(any())).thenReturn(fakeCollectionModel);
    assertEquals(fakeCollectionModel, controller.viewAll(mock(User.class), LocalDateTime.now()));
  }
}
