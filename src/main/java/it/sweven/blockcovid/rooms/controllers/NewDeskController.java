package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.dto.NewDeskInfo;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.exceptions.DeskNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class NewDeskController implements RoomsController {
  private DeskAssembler deskAssembler;
  private DeskService deskService;

  public NewDeskController(DeskAssembler deskAssembler, DeskService deskService) {
    this.deskAssembler = deskAssembler;
    this.deskService = deskService;
  }

  @PostMapping(
      value = "{nameRoom}/desks",
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Desk successfully created"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid desk parameters",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Requested room doesn't exist",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Desk already exist (with the same id or position)",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  @PreAuthorize("#submitter.isAdmin()")
  public CollectionModel<EntityModel<DeskWithRoomName>> addDesk(
      @PathVariable String nameRoom,
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @Valid @NotNull @RequestBody Set<NewDeskInfo> newDesks) {
    List<Desk> addedDesks = new ArrayList<>();
    for (NewDeskInfo desk : newDesks) {
      try {
        addedDesks.add(deskService.addDesk(desk, nameRoom));
      } catch (DeskNotAvailable e) {
        throw new ResponseStatusException(HttpStatus.CONFLICT);
      } catch (RoomNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      } catch (IllegalArgumentException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }
    return deskAssembler
        .setAuthorities(submitter.getAuthorities())
        .toCollectionModel(
            addedDesks.stream()
                .map(d -> new DeskWithRoomName(nameRoom, d.getId(), d.getX(), d.getY()))
                .collect(Collectors.toList()));
  }
}
