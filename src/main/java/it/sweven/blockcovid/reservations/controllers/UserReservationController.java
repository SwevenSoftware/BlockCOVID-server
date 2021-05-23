package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;

  @Autowired
  public UserReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @GetMapping("{username}")
  @PreAuthorize("#submitter.isAdmin()")
  public CollectionModel<EntityModel<ReservationWithRoom>> userReservations(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestParam LocalDateTime from,
      @RequestParam LocalDateTime to,
      @PathVariable String username) {
    return reservationWithRoomAssembler.toCollectionModel(
        reservationService.findByTimeInterval(from, to).stream()
            .parallel()
            .filter(reservationWithRoom -> reservationWithRoom.getUsername().equals(username))
            .collect(Collectors.toList()));
  }
}
