package it.sweven.blockcovid.repositories;

/* Java imports */
import java.util.List;
import java.util.Optional;

/* Spring imports */
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/* Our imports */
import it.sweven.blockcovid.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByToken(String token);
    public Optional<User> findByUsername(String username);
    public List<User> findAll();
}
