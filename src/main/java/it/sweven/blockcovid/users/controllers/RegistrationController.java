package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserRegistrationService;
import javax.security.auth.login.CredentialException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RegistrationController implements UsersController {
  private final UserRegistrationService registrationService;
  private final UserAssembler userAssembler;

  @Autowired
  RegistrationController(UserRegistrationService registrationService, UserAssembler userAssembler) {
    this.registrationService = registrationService;
    this.userAssembler = userAssembler;
  }

  @PostMapping(value = "", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Provided user registered successfully"),
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
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<User> register(
      @RequestBody @Valid CredentialsWithAuthorities credentials,
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    try {
      User registeredUser = registrationService.register(credentials);
      return userAssembler.setAuthorities(submitter.getAuthorities()).toModel(registeredUser);
    } catch (CredentialException exception) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
    } catch (BadCredentialsException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
  }
}
