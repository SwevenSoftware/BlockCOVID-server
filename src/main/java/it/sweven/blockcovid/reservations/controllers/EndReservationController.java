package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.EndUsageInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.AlreadyEnded;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class EndReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public EndReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PutMapping("end/{reservationId}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Reservation successfully modified"),
    @ApiResponse(
        responseCode = "400",
        description = "End already requested for this reservation or end requested before start",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Reservation not found",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Another reservation clashes with yours",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @ResponseBody
  @PreAuthorize("#submitter.isUser()")
  public EntityModel<ReservationWithRoom> endUsage(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String reservationId,
      @RequestBody EndUsageInfo endUsageInfo) {
    ReservationWithRoom reservation;
    try {
      reservation = reservationService.findById(reservationId);
    } catch (NoSuchReservation noSuchReservation) {
      logger.warn(
          "Could not find reservation with id "
              + reservationId
              + " requested by user "
              + submitter.getUsername());
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such reservation");
    }
    if (!reservation.getUsername().equals(submitter.getUsername())) {
      logger.warn(
          "User "
              + submitter.getUsername()
              + " tried to end the reservation of another user, namely "
              + reservation.getUsername());
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "You must be the owner of a reservation in order to end it");
    }
    try {
      return reservationWithRoomAssembler.toModel(
          reservationService.end(
              reservationId,
              LocalDateTime.now(),
              Optional.ofNullable(endUsageInfo.getDeskCleaned()).orElse(false)));
    } catch (NoSuchReservation noSuchReservation) {
      logger.warn(
          "Could not find reservation with id "
              + reservationId
              + " requested by user "
              + submitter.getUsername()
              + " when trying to end it, inconsistent server state?");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such reservation");
    } catch (BadTimeIntervals badTimeIntervals) {
      logger.warn("reservation with id" + reservationId + " can not be ended before it starts");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usage can't end before its start");
    } catch (AlreadyEnded alreadyEnded) {
      logger.warn("reservation with id" + reservationId + " has already ended");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "End already requested for this reservation");
    } catch (ReservationClash reservationClash) {
      logger.warn(
          "reservation with id"
              + reservationId
              + " can not be ended at the provided time, clashes with another reservation");
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Your reservation conflict with another one");
    }
  }
}
