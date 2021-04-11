package it.sweven.blockcovid.rooms.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ViewDeskStateController implements RoomsController {
  private final DeskService deskService;

  @Autowired
  public ViewDeskStateController(DeskService deskService) {
    this.deskService = deskService;
  }

  @GetMapping("/desks/{deskId}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Sucessfully retrivered desk status"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No desk found with such ID",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isUser() or #submitter.isAdmin()")
  public EntityModel<Desk> deskState(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String deskId) {
    try {
      return EntityModel.of(
          deskService.getDeskById(deskId),
          linkTo(methodOn(this.getClass()).deskState(null, deskId)).withRel("self"));
    } catch (DeskNotFoundException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No desk found with such ID");
    }
  }
}
