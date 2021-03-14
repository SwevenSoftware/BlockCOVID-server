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
import it.sweven.blockcovid.services.UserUpdateService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "api/admin")
public class AdminRouter {
  private final UserAuthenticationService authenticationService;
  private final UserAssembler assembler;
  private final UserService userService;
  private final UserUpdateService userUpdateService;

  @Autowired
  AdminRouter(
      UserAuthenticationService authenticationService,
      UserAssembler assembler,
      UserService userService,
      UserUpdateService userUpdateService) {
    this.authenticationService = authenticationService;
    this.assembler = assembler;
    this.userService = userService;
    this.userUpdateService = userUpdateService;
  }

  @PostMapping(
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
    @ApiResponse(responseCode = "400", description = "No credentials provided"),
    @ApiResponse(responseCode = "401", description = "Invalid authentication token"),
    @ApiResponse(responseCode = "404", description = "Username not found")
  })
  public EntityModel<User> modifyUser(
      @RequestHeader String Authorization,
      @PathVariable String username,
      @RequestBody Credentials newCredentials) {
    User admin = authenticationService.authenticateByToken(Authorization);
    if (newCredentials == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No credentials provided");
    User user;
    try {
      user = userService.getByUsername(username);
    } catch (UsernameNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + username + " not found");
    }
    Optional.ofNullable(newCredentials.getPassword())
        .ifPresent(pwd -> userUpdateService.updatePassword(user, pwd));
    Optional.ofNullable(newCredentials.getAuthorities())
        .ifPresent(auth -> userUpdateService.updateAuthorities(user, auth));
    return assembler.setAuthorities(admin.getAuthorities()).toModel(user);
  }
}
