package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.dto.CredentialChangeRequestForm;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ModifyPasswordController implements AccountController {
  private final UserAssembler assembler;
  private final UserService userService;
  private final Logger logger = LoggerFactory.getLogger(ModifyPasswordController.class);

  @Autowired
  public ModifyPasswordController(UserAssembler assembler, UserService userService) {
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
  @Operation(security = @SecurityRequirement(name = "bearer"))
  @PreAuthorize("#user.isEnabled()")
  public EntityModel<User> modifyPassword(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid CredentialChangeRequestForm requestForm) {
    try {
      userService.updatePassword(user, requestForm.getOldPassword(), requestForm.getNewPassword());
    } catch (BadCredentialsException exception) {
      logger.warn("Bad old password provided for user " + user.getUsername());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong or missing credentials");
    }
    return assembler.setAuthorities(user.getAuthorities()).toModel(user);
  }
}
