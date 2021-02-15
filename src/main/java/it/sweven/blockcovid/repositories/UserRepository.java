package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/* Reactor core imports */
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* Our imports */
import it.sweven.blockcovid.entities.User;
import it.sweven.blockcovid.entities.Token;
import it.sweven.blockcovid.entities.LoginForm;
    
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    public Mono<User> findByToken(Token token);
    public Mono<User> findByLogin(LoginForm login);
    public Flux<User> findAll();
}
