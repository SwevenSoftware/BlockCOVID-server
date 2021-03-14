package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.security.Authority;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import it.sweven.blockcovid.services.UserService;
import java.util.Optional;
import javax.security.auth.login.CredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/admin")
public class AdminRouter {
  private final UserAuthenticationService authenticationService;
  private final UserRegistrationService registrationService;
  private final UserAssembler assembler;
  private final UserService userService;

  @Autowired
  AdminRouter(
      UserAuthenticationService authenticationService,
      UserRegistrationService registrationService,
      UserAssembler assembler,
      UserService userService) {
    this.authenticationService = authenticationService;
    this.registrationService = registrationService;
    this.assembler = assembler;
    this.userService = userService;
  }

  @PostMapping(value = "user/new", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> register(
      @RequestBody Credentials credentials, @RequestHeader String Authorization) {
    User admin = authenticationService.authenticateByToken(Authorization);
    if (!admin.getAuthorities().contains(Authority.ADMIN))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Method not allowed");
    try {
      User registeredUser = registrationService.register(credentials);
      EntityModel<User> entityUser =
          assembler.setAuthorities(admin.getAuthorities()).toModel(registeredUser);
      return ResponseEntity.created(entityUser.getRequiredLink(IanaLinkRelations.SELF).toUri())
          .body(entityUser);
    } catch (CredentialException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
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
        .ifPresent(pwd -> userService.updatePassword(user, pwd));
    Optional.ofNullable(newCredentials.getAuthorities())
        .ifPresent(auth -> userService.updateAuthorities(user, auth));
    return assembler.setAuthorities(admin.getAuthorities()).toModel(user);
  }
}
