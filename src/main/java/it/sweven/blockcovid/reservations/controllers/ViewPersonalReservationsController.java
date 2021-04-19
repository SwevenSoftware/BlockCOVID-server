package it.sweven.blockcovid.reservations.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.assemblers.ReservationWithRoomAssembler;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewPersonalReservationsController implements ReservationController {
  private final ReservationService service;
  private final ReservationWithRoomAssembler assembler;

  @Autowired
  public ViewPersonalReservationsController(
      ReservationService service, ReservationWithRoomAssembler assembler) {
    this.service = service;
    this.assembler = assembler;
  }

  @GetMapping("view/personal")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "successfully retrieved all future reservations of user"),
    @ApiResponse(
        responseCode = "400",
        description = "time incorrectly formatted",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "401",
        description = "User not authenticated",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @ResponseBody
  @PreAuthorize("#submitter.isUser() and #submitter.isEnabled()")
  public CollectionModel<EntityModel<ReservationWithRoom>> viewAll(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestParam("from") LocalDateTime timestamp) {
    return assembler.toCollectionModel(
        service.findByUsernameAndStart(submitter.getUsername(), timestamp));
  }
}
