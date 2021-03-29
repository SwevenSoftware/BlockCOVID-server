package it.sweven.blockcovid.routers.admin;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.DeskAssembler;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.DeskWithRoomName;
import it.sweven.blockcovid.entities.room.Desk;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.DeskService;
import java.util.NoSuchElementException;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AdminDeleteDeskRouter implements AdminRouter {

  private final DeskService deskService;
  private final DeskAssembler deskAssembler;

  @Autowired
  public AdminDeleteDeskRouter(DeskService deskService, DeskAssembler deskAssembler) {
    this.deskService = deskService;
    this.deskAssembler = deskAssembler;
  }

  @DeleteMapping("room/{roomName}/desks/delete")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Desk successfully deleted"),
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
  public EntityModel<DeskWithRoomName> delete(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName,
      @NotNull @RequestBody DeskInfo toDelete) {
    try {
      Desk deleted = deskService.deleteDeskByInfosAndRoomName(toDelete, roomName);
      DeskWithRoomName toReturn = new DeskWithRoomName(roomName, deleted.getX(), deleted.getY());
      return deskAssembler.toModel(toReturn);
    } catch (NoSuchElementException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
