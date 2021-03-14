package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "api/user")
public class UserRouter {
  private final UserAuthenticationService authenticationService;
  private final UserAssembler assembler;
  private final UserService userService;

  @Autowired
  UserRouter(
      UserAuthenticationService authenticationService,
      UserAssembler assembler,
      UserService userService) {
    this.authenticationService = authenticationService;
    this.assembler = assembler;
    this.userService = userService;
  }

  @PostMapping(value = "/info", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
    @ApiResponse(responseCode = "401", description = "Invalid authentication token")
  })
  public EntityModel<User> info(@RequestHeader String Authorization) {
    User user = authenticationService.authenticateByToken(Authorization);
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }

  @PostMapping(
      value = "/modify/password",
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Password successfully updated",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))),
    @ApiResponse(responseCode = "400", description = "Password not provided"),
    @ApiResponse(responseCode = "401", description = "Invalid authentication token")
  })
  public EntityModel<User> modifyPassword(
      @RequestHeader String Authorization, @RequestBody Credentials newCredentials) {
    User user = authenticationService.authenticateByToken(Authorization);
    if (newCredentials == null || newCredentials.getPassword() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password not provided");
    else {
      userService.updatePassword(user, newCredentials.getPassword());
      return assembler.setAuthorities(user.getAuthorities()).toModel(user);
    }
  }
}
