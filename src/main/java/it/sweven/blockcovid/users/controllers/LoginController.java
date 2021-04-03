package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.users.assemblers.UserAssembler;
import it.sweven.blockcovid.users.dto.Credentials;
import it.sweven.blockcovid.users.dto.TokenWithAuthorities;
import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.services.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class LoginController implements AccountController {

  private final UserAuthenticationService authenticationService;
  private final UserAssembler assembler;

  @Autowired
  LoginController(UserAuthenticationService authenticationService, UserAssembler userAssembler) {
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
  public EntityModel<TokenWithAuthorities> login(@RequestBody Credentials credentials) {
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
