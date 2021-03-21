package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.services.RoomService;
import it.sweven.blockcovid.services.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/rooms")
public class RoomRouter {

  private final RoomService service;
  private final UserAuthenticationService authenticationService;
  private final RoomAssembler assembler;

  @Autowired
  public RoomRouter(
      RoomService service,
      UserAuthenticationService authenticationService,
      RoomAssembler assembler) {
    this.service = service;
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
  public EntityModel<Room> viewRoom(
      @PathVariable String roomName, @RequestHeader String Authorization) {
    authenticationService.authenticateByToken(Authorization);
    Room requestedRoom;
    try {
      requestedRoom = service.getByName(roomName);
    } catch (RoomNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room named " + roomName);
    }
    return assembler.toModel(requestedRoom);
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
  public CollectionModel<EntityModel<Room>> listRooms(@RequestHeader String Authorization) {
    authenticationService.authenticateByToken(Authorization);
    return assembler.toCollectionModel(service.getAllRooms());
  }
}
