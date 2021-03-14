package it.sweven.blockcovid.routers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.entities.user.Credentials;
import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.repositories.UserRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api")
public class LoginRouter {

  private final UserAuthenticationService authenticationService;
  private final UserRepository userRepository;

  @Autowired
  LoginRouter(
      UserAuthenticationService authenticationService,
      UserRegistrationService registrationService,
      UserRepository userRepository) {
    this.authenticationService = authenticationService;
    this.userRepository = userRepository;
  }

  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public EntityModel<Token> login(@RequestBody Credentials credentials) {
    try {
      return EntityModel.of(
          authenticationService.login(credentials.getUsername(), credentials.getPassword()),
          linkTo(methodOn(LoginRouter.class).login(credentials)).withSelfRel(),
          linkTo(methodOn(AdminRouter.class).register(credentials, "")).withRel("register"));
    } catch (Exception exception) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credentials not found");
    }
  }
}
