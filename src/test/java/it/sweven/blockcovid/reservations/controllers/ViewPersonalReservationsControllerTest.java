package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.reservations.assemblers.ReservationAssembler;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

class ViewPersonalReservationsControllerTest {
  private ViewPersonalReservationsController controller;
  private ReservationService service;
  private ReservationAssembler assembler;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    assembler = mock(ReservationAssembler.class);
    controller = new ViewPersonalReservationsController(service, assembler);
  }

  @Test
  void happyPath() {
    List<Reservation> fakeList = mock(List.class);
    CollectionModel<EntityModel<Reservation>> fakeCollectionModel = mock(CollectionModel.class);
    when(service.findByUsernameAndStart(anyString(), any())).thenReturn(fakeList);
    when(assembler.toCollectionModel(any())).thenReturn(fakeCollectionModel);
    assertEquals(fakeCollectionModel, controller.viewAll(mock(User.class), LocalDateTime.now()));
  }
}
