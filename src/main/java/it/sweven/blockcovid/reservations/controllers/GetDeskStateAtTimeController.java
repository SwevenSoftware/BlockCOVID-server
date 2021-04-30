package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.dto.DeskAvailability;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetDeskStateAtTimeController implements ReservationController {
  private final ReservationService reservationService;

  @Autowired
  public GetDeskStateAtTimeController(ReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @GetMapping("desk/{deskId}/{timestamp}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved desk state"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isUser() or #submitter.isAdmin()")
  @ResponseBody
  public EntityModel<DeskAvailability> getDeskState(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable LocalDateTime timestamp,
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
