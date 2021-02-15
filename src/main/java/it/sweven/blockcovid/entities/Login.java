package it.sweven.blockcovid.entities;

/* Reactor core imports */
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import it.sweven.blockcovid.repositories.UserRepository;

public class Login {    
    public static Mono<User> fromLoginForm(LoginForm login, UserRepository repo) {
	return repo.findByLogin(login);
    }

    public static Mono<User> fromToken(Token token, UserRepository repo) {
	return repo.findByToken(token);
    }
}
