package it.sweven.blockcovid.reservations.controllers;

import it.sweven.blockcovid.reservations.dto.DeskAvailability;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetDeskStateAtTimeController implements ReservationController {
  private final ReservationService reservationService;

  @Autowired
  public GetDeskStateAtTimeController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @GetMapping("desk/{deskId}/{timestamp}")
  @PreAuthorize("#submitter.isEnabled() and #submitter.isUser() or #submitter.isAdmin()")
  public EntityModel<DeskAvailability> getDeskState(
      @AuthenticationPrincipal User submitter,
      @PathVariable @DateTimeFormat(pattern = "yyyyMMdd'T'kkmm") LocalDateTime timestamp,
      @PathVariable String deskId) {
    AtomicReference<DeskAvailability> toReturn = new AtomicReference<>();
    AtomicReference<LocalDateTime> next = new AtomicReference<>();
    reservationService
        .nextReservation(deskId, timestamp)
        .ifPresent(reservation -> next.set(reservation.getStart()));
    reservationService
        .findIfTimeFallsInto(deskId, timestamp)
        .ifPresentOrElse(
            reservation -> toReturn.set(new DeskAvailability(false, reservation.getEnd())),
            () -> toReturn.set(new DeskAvailability(true, next.get())));
    return EntityModel.of(toReturn.get());
  }
}
