package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DeleteUserController implements UsersController {
  private final UserAssembler userAssembler;
  private final UserService userService;

  @Autowired
  public DeleteUserController(UserAssembler userAssembler, UserService userService) {
    this.userAssembler = userAssembler;
    this.userService = userService;
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
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<User> delete(
      @PathVariable String username,
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    try {
      User deletedUser = userService.deleteByUsername(username);
      return userAssembler.toModel(deletedUser);
    } catch (UsernameNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
    }
  }
}
