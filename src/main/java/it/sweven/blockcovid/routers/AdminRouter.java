package it.sweven.blockcovid.routers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.RoomAssembler;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.dto.RoomInfo;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.RoomService;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import it.sweven.blockcovid.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.management.BadAttributeValueExpException;
import javax.security.auth.login.CredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/admin")
public class AdminRouter {
  private final UserAuthenticationService authenticationService;
  private final UserRegistrationService registrationService;
  private final RoomService roomService;
  private final UserAssembler userAssembler;
  private final RoomAssembler roomAssembler;
  private final UserService userService;

  @Autowired
  AdminRouter(
      UserAuthenticationService authenticationService,
      UserRegistrationService registrationService,
      UserAssembler userAssembler,
      UserService userService,
      RoomAssembler roomAssembler,
      RoomService roomService) {
    this.authenticationService = authenticationService;
    this.registrationService = registrationService;
    this.userAssembler = userAssembler;
    this.userService = userService;
    this.roomAssembler = roomAssembler;
    this.roomService = roomService;
  }

  @PostMapping(value = "user/new", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Provided user registered successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
    @ApiResponse(
        responseCode = "400",
        description = "No credentials provided",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Username already taken",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<User> register(
      @RequestBody CredentialsWithAuthorities credentials, @RequestHeader String Authorization) {
    User submitter = authenticationService.authenticateByToken(Authorization);
    if (submitter.getAuthorities().contains(Authority.ADMIN)) {
      try {
        User registeredUser = registrationService.register(credentials);
        return userAssembler.setAuthorities(submitter.getAuthorities()).toModel(registeredUser);
      } catch (CredentialException exception) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
      } catch (NullPointerException exception) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No credentials provided");
      }
    } else
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User has not enough privileges");
  }

  @PutMapping(
      value = "/user/{username}/modify",
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "User details successfully updated",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
    @ApiResponse(
        responseCode = "400",
        description = "No credentials provided",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Username not found",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  public EntityModel<User> modifyUser(
      @RequestHeader String Authorization,
      @PathVariable String username,
      @RequestBody CredentialsWithAuthorities newCredentials) {
    User admin = authenticationService.authenticateByToken(Authorization);
    if (!admin.getAuthorities().contains(Authority.ADMIN))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Method not allowed");
    if (newCredentials == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No credentials provided");
    User user;
    try {
      user = userService.getByUsername(username);
    } catch (UsernameNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found");
    }
    Optional.ofNullable(newCredentials.getPassword())
        .ifPresent(pwd -> userService.setPassword(user, pwd));
    Optional.ofNullable(newCredentials.getAuthorities())
        .ifPresent(auth -> userService.updateAuthorities(user, auth));
    return userAssembler.setAuthorities(admin.getAuthorities()).toModel(user);
  }

  @GetMapping(value = "/users", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "List of existing users"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  public CollectionModel<EntityModel<User>> listUsers(@RequestHeader String Authorization) {
    User admin = authenticationService.authenticateByToken(Authorization);
    if (!admin.getAuthorities().contains(Authority.ADMIN))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Method not allowed");
    List<EntityModel<User>> users =
        userService.getAllUsers().stream()
            .map(u -> userAssembler.setAuthorities(admin.getAuthorities()).toModel(u))
            .collect(Collectors.toList());
    return CollectionModel.of(
        users, linkTo(methodOn(AdminRouter.class).listUsers(null)).withSelfRel());
  }

  @DeleteMapping(value = "/user/{username}", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Username successfully deleted"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Username not found",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  public EntityModel<User> delete(
      @PathVariable String username, @RequestHeader String Authorization) {
    User submitter = authenticationService.authenticateByToken(Authorization);
    if (!submitter.getAuthorities().contains(Authority.ADMIN))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Method not allowed");
    try {
      User deletedUser = userService.deleteByUsername(username);
      return userAssembler.toModel(deletedUser);
    } catch (UsernameNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
    }
  }

  @PostMapping(value = "rooms/new", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Missing or wrong-formatted arguments",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<Room> newRoom(
      @RequestHeader String Authorization, @RequestBody RoomInfo newRoom) {
    User submitter = authenticationService.authenticateByToken(Authorization);
    if (!submitter.getAuthorities().contains(Authority.ADMIN))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Method not allowed");
    try {
      Room createdRoom = roomService.createRoom(newRoom);
      return roomAssembler.toModel(createdRoom);
    } catch (BadAttributeValueExpException exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Missing or wrong-formatted arguments");
    }
  }
}
