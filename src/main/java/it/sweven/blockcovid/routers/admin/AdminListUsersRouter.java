package it.sweven.blockcovid.routers.admin;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminListUsersRouter implements AdminRouter {

  private final UserService userService;
  private final UserAssembler userAssembler;

  @Autowired
  public AdminListUsersRouter(UserService userService, UserAssembler userAssembler) {
    this.userService = userService;
    this.userAssembler = userAssembler;
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
  @PreAuthorize("#submitter.isAdmin()")
  public CollectionModel<EntityModel<User>> listUsers(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    List<EntityModel<User>> users =
        userService.getAllUsers().stream()
            .map(u -> userAssembler.setAuthorities(submitter.getAuthorities()).toModel(u))
            .collect(Collectors.toList());
    return CollectionModel.of(
        users, linkTo(methodOn(AdminListUsersRouter.class).listUsers(null)).withSelfRel());
  }
}
