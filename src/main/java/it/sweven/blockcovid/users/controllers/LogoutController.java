package it.sweven.blockcovid.users.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class LogoutController implements AccountController {
  private final TokenService tokenService;

  @Autowired
  public LogoutController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @DeleteMapping("logout")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "user successfully logged out"),
    @ApiResponse(
        responseCode = "400",
        description = "Token not found",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  public EntityModel<Token> logout(@Parameter(hidden = true) @RequestHeader String Authorization) {
    try {
      return EntityModel.of(tokenService.delete(Authorization));
    } catch (AuthenticationException exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token not found");
    }
  }
}
