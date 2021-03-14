package it.sweven.blockcovid.routers;

import it.sweven.blockcovid.assemblers.AdminUserModelAssembler;
import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.security.Authority;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import javax.security.auth.login.CredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/admin")
public class AdminRouter {
  private final UserAuthenticationService authenticationService;
  private final UserRegistrationService registrationService;
  private final AdminUserModelAssembler adminUserModelAssembler;

  @Autowired
  AdminRouter(
      UserAuthenticationService authenticationService,
      UserRegistrationService registrationService,
      AdminUserModelAssembler adminUserModelAssembler) {
    this.authenticationService = authenticationService;
    this.registrationService = registrationService;
    this.adminUserModelAssembler = adminUserModelAssembler;
  }

  @PostMapping(value = "user/new", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> register(
      @RequestBody Credentials credentials, @RequestHeader String Authorization) {
    if (authenticationService
        .authenticateByToken(Authorization)
        .getAuthorities()
        .contains(Authority.ADMIN)) {
      try {
        User registeredUser = registrationService.register(credentials);
        EntityModel<User> entityUser = adminUserModelAssembler.toModel(registeredUser);
        return ResponseEntity.created(entityUser.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityUser);
      } catch (CredentialException exception) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
      }

    } else
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User has not enough privileges");
  }
}
