package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.MongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    /* l'implementazione può rimanere vuota, La classe di base si
     * occupa di fare tutto */
}
