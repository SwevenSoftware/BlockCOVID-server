package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.RoomAssembler;
import it.sweven.blockcovid.rooms.dto.RoomInfo;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
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
    @ApiResponse(responseCode = "400", description = "Method not allowed")
  })
  @PreAuthorize("#submitter.isEnabled() and #submitter.isAdmin()")
  public EntityModel<Room> modifyRoom(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName,
      @NotNull @RequestBody RoomInfo roomInfo) {
    try {
      Room room = roomService.getByName(roomName);
      Optional.ofNullable(roomInfo.getName()).ifPresent(room::setName);
      Optional.ofNullable(roomInfo.getOpeningAt()).ifPresent(room::setOpeningTime);
      Optional.ofNullable(roomInfo.getClosingAt()).ifPresent(room::setClosingTime);
      Optional.ofNullable(roomInfo.getOpeningDays()).ifPresent(room::setOpeningDays);
      Optional.ofNullable(roomInfo.getWidth()).ifPresent(room::setWidth);
      Optional.ofNullable(roomInfo.getHeight()).ifPresent(room::setHeight);
      return roomAssembler.toModel(roomService.save(room));
    } catch (RoomNotFoundException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
