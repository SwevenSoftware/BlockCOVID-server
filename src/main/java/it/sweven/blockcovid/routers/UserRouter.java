package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialChangeRequestForm;
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

  @GetMapping(value = "/info", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200"),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<User> info(@RequestHeader String Authorization) {
    User user = authenticationService.authenticateByToken(Authorization);
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }

  @PutMapping(
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
    @ApiResponse(
        responseCode = "400",
        description = "Wrong or missing credentials",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid authentication token",
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<User> modifyPassword(
      @RequestHeader String Authorization, @RequestBody CredentialChangeRequestForm requestForm) {
    if (requestForm == null
        || requestForm.getNewPassword() == null
        || requestForm.getOldPassword() == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong or missing credentials");
    User user = authenticationService.authenticateByToken(Authorization);
    userService.updatePassword(user, requestForm.getOldPassword(), requestForm.getNewPassword());
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }
}
