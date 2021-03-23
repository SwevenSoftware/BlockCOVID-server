package it.sweven.blockcovid.routers.user;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialChangeRequestForm;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "api/user")
public class UserRouter {
  private final UserAuthenticationService authenticationService;
  private final UserAssembler assembler;
  private final UserService userService;

  @Autowired
  public UserRouter(
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
  @PreAuthorize("#user.isUser()")
  public EntityModel<User> info(@AuthenticationPrincipal User user) {
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
  @PreAuthorize("#user.isEnabled()")
  public EntityModel<User> modifyPassword(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid CredentialChangeRequestForm requestForm) {
    userService.updatePassword(user, requestForm.getOldPassword(), requestForm.getNewPassword());
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }
}
