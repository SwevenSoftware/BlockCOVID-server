package it.sweven.blockcovid.routers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.assemblers.UserAssembler;
import it.sweven.blockcovid.dto.Credentials;
import it.sweven.blockcovid.dto.TokenWithAuthorities;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.UserAuthenticationService;
import javax.validation.Valid;
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
  private final UserAssembler assembler;

  @Autowired
  LoginRouter(UserAuthenticationService authenticationService, UserAssembler userAssembler) {
    this.authenticationService = authenticationService;
    this.assembler = userAssembler;
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
        content = @Content(schema = @Schema(implementation = ResponseStatusException.class)))
  })
  public EntityModel<TokenWithAuthorities> login(@Valid @RequestBody Credentials credentials) {
    try {
      Token loginToken =
          authenticationService.login(credentials.getUsername(), credentials.getPassword());
      User loggedInUser = authenticationService.authenticateByToken(loginToken.getId());
      TokenWithAuthorities toReturn =
          new TokenWithAuthorities(loginToken, loggedInUser.getAuthorities());
      return EntityModel.of(
          toReturn,
          assembler.setAuthorities(loggedInUser.getAuthorities()).toModel(loggedInUser).getLinks());
    } catch (BadCredentialsException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
  }
}
