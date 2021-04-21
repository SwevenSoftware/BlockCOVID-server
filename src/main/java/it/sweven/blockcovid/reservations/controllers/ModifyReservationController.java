package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.entities.ReservationBuilder;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.util.Optional;
import javax.management.BadAttributeValueExpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ModifyReservationController implements ReservationController {
  private final ReservationService service;
  private final ReservationWithRoomAssembler assembler;
  private final ReservationBuilder reservationBuilder;

  @Autowired
  public ModifyReservationController(
      ReservationService service,
      ReservationWithRoomAssembler assembler,
      ReservationBuilder reservationBuilder) {
    this.service = service;
    this.assembler = assembler;
    this.reservationBuilder = reservationBuilder;
  }

  @PutMapping("reservation/{idReservation}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Reservation successfully modified"),
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
  @PreAuthorize("#submitter.isUser() or #submitter.isAdmin()")
  public EntityModel<ReservationWithRoom> modifyReservation(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String idReservation,
      @RequestBody ReservationInfo reservationInfo) {
    ReservationWithRoom requested;
    try {
      requested = service.findById(idReservation);
    } catch (NoSuchReservation e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    if (submitter.isUser() && !requested.getUsername().equals(submitter.getUsername()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    Reservation toModify;
    try {
      toModify = reservationBuilder.from(requested).build();
    } catch (BadAttributeValueExpException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    Optional.ofNullable(reservationInfo.getDeskId()).ifPresent(toModify::setDeskId);
    Optional.ofNullable(reservationInfo.getStart()).ifPresent(toModify::setStart);
    Optional.ofNullable(reservationInfo.getEnd()).ifPresent(toModify::setEnd);
    try {
      return assembler.toModel(service.save(toModify));
    } catch (ReservationClash reservationClash) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }
  }
}
