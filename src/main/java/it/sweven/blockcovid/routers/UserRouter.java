package it.sweven.blockcovid.routers;

/* Spring Annotations */
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/* Bean factory annotations */
import org.springframework.beans.factory.annotation.Autowired;

/* Reactor core imports */
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* Our own imports */
import it.sweven.blockcovid.entities.*;
import it.sweven.blockcovid.repositories.*;

@RestController
class UserRouter {
    
    @Autowired
    private final UserRepository repository;

    UserRouter(UserRepository repo) {
	this.repository = repo;
    }

    @PostMapping("/login")
    @ResponseBody
    String login(@RequestBody LoginFrom loginForm) {
	
    }

    @GetMapping("/user/{name}")
    Mono<User> name(@PathVariable String name) {
	return repository.findById(name);
    }

    @PostMapping("/user/new")
    @ResponseBody
    Mono<User> insert(@RequestBody User newUser) {
	return repository.save(newUser);
    }

    @GetMapping("/user/all")
    Flux<User> all(){
	return repository.findAll();
    }
}
