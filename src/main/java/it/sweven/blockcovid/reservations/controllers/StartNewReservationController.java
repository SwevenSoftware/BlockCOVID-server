package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import javax.management.BadAttributeValueExpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StartNewReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationWithRoomAssembler reservationWithRoomAssembler;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public StartNewReservationController(
      ReservationService reservationService,
      ReservationWithRoomAssembler reservationWithRoomAssembler) {
    this.reservationService = reservationService;
    this.reservationWithRoomAssembler = reservationWithRoomAssembler;
  }

  @PostMapping("start")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "reservation successfully made"),
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
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isUser() or #submitter.isAdmin()")
  @ResponseBody
  public EntityModel<ReservationWithRoom> start(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestBody ReservationInfo reservationInfo) {
    LocalDateTime now = LocalDateTime.now();
    if (reservationInfo.getEnd() == null || reservationInfo.getDeskId() == null) {
      logger.warn("Invalid attempt of starting a reservation, no end or deskId provided");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "you must supply deskId and reservation end");
    }
    reservationInfo.setStart(now);
    if (reservationInfo.getStart().isAfter(reservationInfo.getEnd())) {
      logger.warn(
          "A reservation was not started due to bad request, it could not start after the end");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "You can not start a new reservation that ends in the past");
    }
    try {
      ReservationWithRoom newReservation =
          reservationService.addReservation(reservationInfo, submitter.getUsername());
      return reservationWithRoomAssembler.toModel(
          reservationService.start(newReservation.getId(), now));
    } catch (ReservationClash reservationClash) {
      logger.warn(
          "A reservation start request was blocked due to conflict with another reservation");
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Your reservation clashes with some other");
    } catch (BadTimeIntervals badTimeIntervals) {
      logger.warn(
          "A reservation start request was blocked, the desk is not available for the whole reservation");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The desk is not available throughout the whole reservation");
    } catch (BadAttributeValueExpException e) {
      logger.warn("reservation " + reservationInfo + " was not started due to bad input values");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad input values");
    } catch (StartingTooEarly startingTooEarly) {
      logger.warn(
          "reservation "
              + reservationInfo
              + " was not started since its start would have been more than 30 minutes before the reserved start");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "You can start your reservation maximum 30 minutes before the starting time");
    } catch (NoSuchReservation noSuchReservation) {
      logger.warn("Reservation not found");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found");
    } catch (Exception exception) {
      logger.warn("A user tried to book a non existent desk in a non existing room");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "You are trying to book a non existing desk in a non existing room");
    }
  }
}
