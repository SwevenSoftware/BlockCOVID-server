package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNameNotAvailable;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
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
public class ModifyRoomController implements RoomsController {

  private final RoomService roomService;
  private final RoomAssembler roomAssembler;

  @Autowired
  public ModifyRoomController(RoomService roomService, RoomAssembler roomAssembler) {
    this.roomService = roomService;
    this.roomAssembler = roomAssembler;
  }

  @PutMapping("/{roomName}")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Room successfully updated"),
    @ApiResponse(responseCode = "404", description = "No room found with given name"),
    @ApiResponse(responseCode = "400", description = "Method not allowed"),
    @ApiResponse(responseCode = "409", description = "New room name not available")
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isAdmin()")
  public EntityModel<Room> modifyRoom(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName,
      @NotNull @RequestBody RoomInfo roomInfo) {
    try {
      return roomAssembler.toModel(roomService.updateRoom(roomName, roomInfo));
    } catch (RoomNotFoundException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found with given name");
    } catch (RoomNameNotAvailable e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "New room name not available");
    }
  }
}
