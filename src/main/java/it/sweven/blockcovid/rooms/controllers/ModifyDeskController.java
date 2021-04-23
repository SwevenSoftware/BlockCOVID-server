package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.DeskAssembler;
import it.sweven.blockcovid.rooms.dto.DeskModifyInfo;
import it.sweven.blockcovid.rooms.dto.DeskWithRoomName;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.users.entities.User;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ModifyDeskController implements RoomsController {
  private final DeskService deskService;
  private final DeskAssembler deskAssembler;

  @Autowired
  public ModifyDeskController(DeskService deskService, DeskAssembler deskAssembler) {
    this.deskService = deskService;
    this.deskAssembler = deskAssembler;
  }

  @PutMapping("/{roomName}/desks")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Desk successfully modified"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Room or desk not found",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isAdmin()")
  public EntityModel<DeskWithRoomName> modifyDesk(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName,
      @NotNull @RequestBody DeskModifyInfo modifyInfo) {
    try {
      Desk toModify = deskService.getDeskByInfoAndRoomName(modifyInfo.getOldInfo(), roomName);
      Optional.ofNullable(modifyInfo.getNewInfo().getX()).ifPresent(toModify::setX);
      Optional.ofNullable(modifyInfo.getNewInfo().getY()).ifPresent(toModify::setY);
      toModify = deskService.update(toModify);
      DeskWithRoomName toReturn =
          new DeskWithRoomName(roomName, toModify.getId(), toModify.getX(), toModify.getY());
      return deskAssembler.toModel(toReturn);
    } catch (NoSuchElementException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
