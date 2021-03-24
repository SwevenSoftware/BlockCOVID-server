package it.sweven.blockcovid.routers.user;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.CredentialChangeRequestForm;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserModifyPasswordRouter implements UserRouter {
  private final UserAssembler assembler;
  private final UserService userService;

  @Autowired
  public UserModifyPasswordRouter(UserAssembler assembler, UserService userService) {
    this.assembler = assembler;
    this.userService = userService;
  }

  @PutMapping(
      value = "/modify/password",
      consumes = "application/json",
      produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Password successfully updated"),
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
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid CredentialChangeRequestForm requestForm) {
    userService.updatePassword(user, requestForm.getOldPassword(), requestForm.getNewPassword());
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }
}
