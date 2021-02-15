package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    public Mono<User> findByToken(Mono<Token> token);
}
