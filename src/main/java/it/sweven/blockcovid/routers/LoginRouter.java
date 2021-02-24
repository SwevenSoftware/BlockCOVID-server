package it.sweven.blockcovid.routers;

/* Spring imports */

import it.sweven.blockcovid.repositories.UserRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
class LoginRouter {

  @Autowired private final UserRepository userRepository;
  @Autowired private UserAuthenticationService authenticationService;
  @Autowired private UserRegistrationService registrationService;

  LoginRouter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  String login(
      @RequestParam("username") String username, @RequestParam("password") String password) {
    try {
      return authenticationService.login(username, password);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @PostMapping("/register")
  public String register(
      @RequestParam("username") String username, @RequestParam("password") String password) {
    try {
      return registrationService.register(username, password);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
