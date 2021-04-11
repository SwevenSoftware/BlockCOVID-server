package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
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
public class NewReservationController implements ReservationController {
  private final ReservationService reservationService;
  private final ReservationAssembler reservationAssembler;

  @Autowired
  public NewReservationController(
      ReservationService reservationService, ReservationAssembler reservationAssembler) {
    this.reservationService = reservationService;
    this.reservationAssembler = reservationAssembler;
  }

  @PostMapping("new")
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
        responseCode = "409",
        description = "Another reservation clashes with yours",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @ResponseBody
  @PreAuthorize("#submitter.isUser()")
  public EntityModel<Reservation> book(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestBody ReservationInfo reservationInfo) {
    if (reservationInfo.isValid()) {
      try {
        return reservationAssembler.toModel(
            reservationService.addReservation(reservationInfo, submitter.getUsername()));
      } catch (ReservationClash reservationClash) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT, "Another reservation for the same desk clashes with yours");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid fields in your request");
    }
  }
}
