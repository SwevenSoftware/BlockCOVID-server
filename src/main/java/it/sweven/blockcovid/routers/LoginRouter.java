package it.sweven.blockcovid.routers;

/* Java imports */
import java.util.List;
import java.util.Optional;

/* Spring Annotations */
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/* Our own imports */
import it.sweven.blockcovid.entities.User;
import it.sweven.blockcovid.repositories.UserRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;

@RestController
class LoginRouter {
    
    @Autowired
    private final UserRepository repository;
    @Autowired
    private UserAuthenticationService authenticationService;
    @Autowired
    private UserRegistrationService registrationService;

    LoginRouter(UserRepository repository) {
	this.repository = repository;
    }

    @PostMapping("/login")
    Object login(@RequestParam("username") String username,
		 @RequestParam("password") String password) {
	try {
            return authenticationService.login(username, password);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(e.getMessage());
        }	
    }

    @PostMapping("/register")
    public Object register(@RequestParam("username") String username,
			   @RequestParam("password") String password) {
        try {
            return registrationService
		.register(username, password);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
