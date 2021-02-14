package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    /* l'implementazione può rimanere vuota, La classe di base si
     * occupa di fare tutto */
}
