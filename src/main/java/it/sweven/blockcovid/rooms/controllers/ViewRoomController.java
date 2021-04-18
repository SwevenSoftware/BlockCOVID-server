package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.rooms.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfoAvailability;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ViewRoomController implements RoomsController {

  private final RoomService roomService;
  private final DeskService deskService;
  private final ReservationService reservationService;
  private final RoomWithDesksAssembler assembler;

  @Autowired
  public ViewRoomController(
      RoomService roomService,
      DeskService deskService,
      ReservationService reservationService,
      RoomWithDesksAssembler assembler) {
    this.roomService = roomService;
    this.deskService = deskService;
    this.reservationService = reservationService;
    this.assembler = assembler;
  }

  @GetMapping("/{roomName}")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Requested room successfully provided"),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No room found with such name",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<RoomWithDesks> viewRoom(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String roomName) {
    Room requestedRoom;
    try {
      requestedRoom = roomService.getByName(roomName);
    } catch (RoomNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room named " + roomName);
    }
    List<DeskInfoAvailability> associatedDesks =
        deskService.getDesksByRoom(roomName).stream()
            .map(
                d -> {
                  boolean available =
                      reservationService
                          .findIfTimeFallsInto(d.getId(), LocalDateTime.now())
                          .isEmpty();
                  return new DeskInfoAvailability(d.getId(), d.getX(), d.getY(), available);
                })
            .collect(Collectors.toList());
    return assembler.toModel(new RoomWithDesks(requestedRoom, associatedDesks));
  }
}
