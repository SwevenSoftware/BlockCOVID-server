package it.sweven.blockcovid.reservations.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

class UserReservationControllerTest {
  private UserReservationController controller;
  private ReservationService reservationService;
  private ReservationWithRoomAssembler assembler;
  private List<ReservationWithRoom> fakeReservations;

  @BeforeEach
  void setUp() {
    reservationService = mock(ReservationService.class);
    fakeReservations =
        List.of(
            mock(ReservationWithRoom.class),
            mock(ReservationWithRoom.class),
            mock(ReservationWithRoom.class),
            mock(ReservationWithRoom.class));
    fakeReservations.forEach(reservation -> when(reservation.getUsername()).thenReturn("user"));
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(fakeReservations);
    assembler =
        spy(
            new ReservationWithRoomAssembler() {
              @Override
              public EntityModel<ReservationWithRoom> toModel(ReservationWithRoom entity) {
                return EntityModel.of(entity);
              }

              @Override
              public CollectionModel<EntityModel<ReservationWithRoom>> toCollectionModel(
                  Iterable<? extends ReservationWithRoom> entities) {
                return CollectionModel.of(
                    StreamSupport.stream(entities.spliterator(), true)
                        .map(this::toModel)
                        .collect(Collectors.toList()));
              }
            });

    controller = new UserReservationController(reservationService, assembler);
  }

  @Test
  void happyPath() {
    CollectionModel<EntityModel<ReservationWithRoom>> expected =
        assembler.toCollectionModel(fakeReservations);
    assertEquals(
        expected,
        controller.userReservations(
            mock(User.class), LocalDateTime.MIN, LocalDateTime.MAX, "user"));
  }

  @Test
  void differentUserReservationsGetExcluded() {
    when(fakeReservations.get(0).getUsername()).thenReturn("admin");
    CollectionModel<EntityModel<ReservationWithRoom>> expected =
        assembler.toCollectionModel(fakeReservations.subList(1, fakeReservations.size()));

    assertEquals(
        expected,
        controller.userReservations(
            mock(User.class), LocalDateTime.MIN, LocalDateTime.MAX, "user"));
  }

  @Test
  void invalidTestReturnsEmptyList() {
    assertTrue(
        controller
            .userReservations(mock(User.class), LocalDateTime.MIN, LocalDateTime.MAX, "admin")
            .getContent()
            .isEmpty());
  }
}
