package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.dto.DeskInfo;
import it.sweven.blockcovid.dto.RoomWithDesks;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.DeskService;
import it.sweven.blockcovid.services.RoomService;
import it.sweven.blockcovid.services.UserAuthenticationService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/rooms")
public class RoomRouter {

  private final RoomService roomService;
  private final DeskService deskService;
  private final UserAuthenticationService authenticationService;
  private final RoomAssembler assembler;

  @Autowired
  public RoomRouter(
      RoomService roomService,
      DeskService deskService,
      UserAuthenticationService authenticationService,
      RoomAssembler assembler) {
    this.roomService = roomService;
    this.deskService = deskService;
    this.authenticationService = authenticationService;
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
      @PathVariable String roomName, @RequestHeader String Authorization) {
    authenticationService.authenticateByToken(Authorization);
    Room requestedRoom;
    try {
      requestedRoom = roomService.getByName(roomName);
    } catch (RoomNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room named " + roomName);
    }
    List<DeskInfo> associatedDesks =
        deskService.getDesksByRoom(roomName).stream()
            .map(d -> new DeskInfo(d.getId(), d.getX(), d.getY()))
            .collect(Collectors.toList());
    return assembler.toModel(new RoomWithDesks(requestedRoom, associatedDesks));
  }

  @GetMapping("")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of all existing rooms successfully provided"),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public CollectionModel<EntityModel<RoomWithDesks>> listRooms(
      @RequestHeader String Authorization) {
    authenticationService.authenticateByToken(Authorization);
    ArrayList<RoomWithDesks> requestedRooms = new ArrayList<>();
    for (Room room : roomService.getAllRooms()) {
      requestedRooms.add(
          new RoomWithDesks(
              room,
              deskService.getDesksByRoom(room.getName()).stream()
                  .map(d -> new DeskInfo(d.getId(), d.getX(), d.getY()))
                  .collect(Collectors.toList())));
    }
    return assembler.toCollectionModel(requestedRooms);
  }
}
