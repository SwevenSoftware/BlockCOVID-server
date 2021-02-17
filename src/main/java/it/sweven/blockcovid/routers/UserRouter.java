package it.sweven.blockcovid.routers;

/* Java imports */
import java.util.List;

/* Spring Annotations */
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/* Bean factory annotations */
import org.springframework.beans.factory.annotation.Autowired;

/* Our own imports */
import it.sweven.blockcovid.entities.User.User;
import it.sweven.blockcovid.entities.User.LoginForm;
import it.sweven.blockcovid.repositories.UserRepository;

@RestController
class UserRouter {
    
    @Autowired
    private final UserRepository repository;

    UserRouter(UserRepository repository) {
	this.repository = repository;
    }

    @PostMapping("/login")
    @ResponseBody
    User login(@RequestBody LoginForm loginForm) {
	return repository.findByLogin(loginForm);
    }

    @GetMapping("/user/{name}")
    User name(@PathVariable String name) {
	return repository.findByUsername(name);
    }

    @PostMapping("/user/new")
    @ResponseBody
    User insert(@RequestBody User newUser) {
	return repository.save(newUser);
    }

    @GetMapping("/user/all")
    List<User> all(){
	return repository.findAll();
    }
}
