package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.AdminController;
import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import javax.management.BadAttributeValueExpException;
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
public class AdminNewRoomController implements AdminController {
  private final RoomService roomService;
  private final RoomAssembler roomAssembler;

  @Autowired
  public AdminNewRoomController(RoomAssembler roomAssembler, RoomService roomService) {
    this.roomService = roomService;
    this.roomAssembler = roomAssembler;
  }

  @PostMapping(value = "rooms/new", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Room successfully created"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Missing or wrong-formatted arguments",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<Room> newRoom(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestBody RoomInfo newRoom) {
    try {
      Room createdRoom = roomService.createRoom(newRoom);
      return roomAssembler.toModel(createdRoom);
    } catch (BadAttributeValueExpException exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing or wrong-formatted arguments");
    }
  }
}
