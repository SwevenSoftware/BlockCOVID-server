package it.sweven.blockcovid.routers.cleaner;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.room.Status;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CleanerMarkRoomAsCleanRouter implements CleanerRouter {
  private final RoomService roomService;
  private final RoomAssembler assembler;

  @Autowired
  CleanerMarkRoomAsCleanRouter(RoomAssembler assembler, RoomService roomService) {
    this.roomService = roomService;
    this.assembler = assembler;
  }

  @PutMapping("/{roomName}/clean")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Room status successfully marked as CLEAN"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Room not found",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isEnabled() and (#submitter.isCleaner() or #submitter.isAdmin())")
  public EntityModel<Room> markAsClean(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName) {
    try {
      return assembler
          .setAuthorities(submitter.getAuthorities())
          .toModel(roomService.setStatus(roomName, Status.CLEAN));
    } catch (RoomNotFoundException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
