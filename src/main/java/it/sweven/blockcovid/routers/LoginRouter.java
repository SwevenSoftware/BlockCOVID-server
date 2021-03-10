package it.sweven.blockcovid.routers;

import it.sweven.blockcovid.entities.User;
import it.sweven.blockcovid.repositories.UserRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
  String login(@RequestBody User user) {
    try {
      return authenticationService.login(user.getUsername(), user.getPassword());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
  @ResponseBody
  public String register(@RequestBody User user) {
    try {
      return registrationService.register(
          user.getUsername(), user.getPassword(), user.getAuthorities());
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
