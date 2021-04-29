package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StartReservationController implements ReservationController {

  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;

  @Autowired
  public StartReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PutMapping("start/{reservationID}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "reservation successfully started"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Not Found",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Another reservation conflicts with yours",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "50",
        description = "Your reservation starts too early (max: 30 min)",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isUser() or #submitter.isAdmin()")
  @ResponseBody
  public EntityModel<ReservationWithRoom> start(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String reservationID) {
    try {
      LocalDateTime now = LocalDateTime.now();
      ReservationWithRoom reservation = reservationService.findById(reservationID);
      if (reservation.getUsageStart() != null)
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Trying to start an already started reservation");
      if (reservation.isEnded())
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Trying to start an already ended reservation");
      if (!reservation.getUsername().equals(submitter.getUsername()))
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, "You must be the owner of a reservation in order to start it");
      return reservationWithRoomAssembler.toModel(reservationService.start(reservationID, now));
    } catch (NoSuchReservation noSuchReservation) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such reservation");
    } catch (ReservationClash reservationClash) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "your reservation conflict with another one");
    } catch (StartingTooEarly startingTooEarly) {
      throw new ResponseStatusException(
          HttpStatus.TOO_EARLY, "you can start at maximum 30 minutes early");
    }
  }
}
