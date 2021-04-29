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
      @RequestBody ReservationInfo reservationInfo) {
    LocalDateTime now = LocalDateTime.now();
    if (reservationInfo.getEnd() == null || reservationInfo.getDeskId() == null)
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "you must supply deskId and reservation end");
    reservationInfo.setStart(now);
    if (reservationInfo.getStart().isAfter(reservationInfo.getEnd()))
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "You can not start a new reservation that ends in the past");
    try {
      ReservationWithRoom newReservation =
          reservationService.addReservation(reservationInfo, submitter.getUsername());
      return reservationWithRoomAssembler.toModel(
          reservationService.start(newReservation.getId(), now));
    } catch (ReservationClash reservationClash) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Your reservation clashes with some other");
    } catch (BadTimeIntervals badTimeIntervals) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The desk is not available throughout the whole reservation");
    } catch (BadAttributeValueExpException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad input values");
    } catch (StartingTooEarly startingTooEarly) {
      throw new ResponseStatusException(
          HttpStatus.TOO_EARLY,
          "You can start your reservation maximum 30 minutes before the starting time");
    } catch (NoSuchReservation noSuchReservation) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "reservation not found");
    } catch (Exception exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "You are trying to book a non existing desk in a non existing room");
    }
  }
}
