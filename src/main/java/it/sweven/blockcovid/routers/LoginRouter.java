package it.sweven.blockcovid.routers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.services.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api")
public class LoginRouter {

  private final UserAuthenticationService authenticationService;

  @Autowired
  LoginRouter(UserAuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  @ResponseBody
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Authenticated Successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Token.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid username or password",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  public EntityModel<Token> login(@RequestBody Credentials credentials) {
    try {
      return EntityModel.of(
          authenticationService.login(credentials.getUsername(), credentials.getPassword()),
          linkTo(methodOn(LoginRouter.class).login(credentials)).withSelfRel(),
          linkTo(methodOn(AdminRouter.class).register(credentials, "")).withRel("register"));
    } catch (BadCredentialsException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
  }
}
