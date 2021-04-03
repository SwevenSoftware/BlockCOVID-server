package it.sweven.blockcovid.rooms.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.rooms.assemblers.RoomWithDesksAssembler;
import it.sweven.blockcovid.rooms.dto.DeskInfo;
import it.sweven.blockcovid.rooms.dto.RoomWithDesks;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.DeskService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ListRoomsController implements RoomsController {

  private final RoomService roomService;
  private final DeskService deskService;
  private final RoomWithDesksAssembler assembler;

  @Autowired
  public ListRoomsController(
      RoomService roomService, DeskService deskService, RoomWithDesksAssembler assembler) {
    this.roomService = roomService;
    this.deskService = deskService;
    this.assembler = assembler;
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
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    ArrayList<RoomWithDesks> requestedRooms = new ArrayList<>();
    for (Room room : roomService.getAllRooms()) {
      requestedRooms.add(
          new RoomWithDesks(
              room,
              deskService.getDesksByRoom(room.getName()).stream()
                  .map(d -> new DeskInfo(d.getX(), d.getY()))
                  .collect(Collectors.toList())));
    }
    return assembler.toCollectionModel(requestedRooms);
  }
}
