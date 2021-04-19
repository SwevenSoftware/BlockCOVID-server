package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DeleteReservationController implements ReservationController {
  private final ReservationService service;
  private final ReservationWithRoomAssembler assembler;

  @Autowired
  public DeleteReservationController(
      ReservationService service, ReservationWithRoomAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @DeleteMapping("reservation/{idReservation}")
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
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isUser() or #submitter.isAdmin()")
  @ResponseBody
  public EntityModel<ReservationWithRoom> deleteReservation(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String idReservation) {
    try {
      ReservationWithRoom toDelete = service.findById(idReservation);
      if (submitter.isUser() && !toDelete.getUsername().equals(submitter.getUsername()))
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      return assembler.toModel(service.delete(idReservation));
    } catch (NoSuchReservation e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
