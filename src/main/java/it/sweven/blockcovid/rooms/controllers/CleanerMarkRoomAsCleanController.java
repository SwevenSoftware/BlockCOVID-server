package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
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
public class CleanerMarkRoomAsCleanController implements CleanerController {
  private final RoomService roomService;
  private final RoomAssembler assembler;

  @Autowired
  CleanerMarkRoomAsCleanController(RoomAssembler assembler, RoomService roomService) {
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
