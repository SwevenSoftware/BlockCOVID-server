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
class LoginRouter {

  private final UserAuthenticationService authenticationService;
  private final UserRegistrationService registrationService;
  private final UserRepository userRepository;

  @Autowired
  LoginRouter(
      UserAuthenticationService authenticationService,
      UserRegistrationService registrationService,
      UserRepository userRepository) {
    this.authenticationService = authenticationService;
    this.registrationService = registrationService;
    this.userRepository = userRepository;
  }

  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public EntityModel<Token> login(@RequestBody Credentials credentials) {
    System.out.println(userRepository.findAll());
    return EntityModel.of(
        authenticationService.login(credentials.getUsername(), credentials.getPassword()),
        linkTo(methodOn(LoginRouter.class).login(credentials)).withSelfRel(),
        linkTo(methodOn(LoginRouter.class).register(credentials)).withRel("register"));
  }

  @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public EntityModel<Token> register(@RequestBody Credentials credentials) {
    try {
      return EntityModel.of(
          registrationService.register(credentials),
          linkTo(methodOn(LoginRouter.class).register(credentials)).withSelfRel(),
          linkTo(methodOn(LoginRouter.class).login(credentials)).withRel("login"));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
