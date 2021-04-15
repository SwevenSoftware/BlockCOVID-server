package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationAssembler;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

class ViewAllReservationsControllerTest {

  private ReservationService service;
  private ViewAllReservationsController controller;

  @BeforeEach
  void setUp() {
    service = mock(ReservationService.class);
    ReservationAssembler assembler =
        spy(
            new ReservationAssembler() {
              @Override
              public EntityModel<Reservation> toModel(Reservation entity) {
                return EntityModel.of(entity);
              }
            });
    controller = new ViewAllReservationsController(service, assembler);
  }

  @Test
  void viewAll() {
    LocalDateTime providedStart = LocalDateTime.now().plusMinutes(20),
        providedEnd = LocalDateTime.now().plusMinutes(60);
    List<Reservation> expectedList =
        List.of(mock(Reservation.class), mock(Reservation.class), mock(Reservation.class));
    when(service.findByTimeInterval(providedStart, providedEnd)).thenReturn(expectedList);
    assertEquals(
        expectedList,
        controller.viewAll(mock(User.class), providedStart, providedEnd).getContent().stream()
            .map(EntityModel::getContent)
            .collect(Collectors.toList()));
  }
}
