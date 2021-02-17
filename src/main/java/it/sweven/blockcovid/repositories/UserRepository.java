package it.sweven.blockcovid.repositories;

/* Java imports */
import java.util.List;

/* Spring imports */
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/* Our imports */
import it.sweven.blockcovid.entities.User.User;
import it.sweven.blockcovid.entities.User.Token;
import it.sweven.blockcovid.entities.User.LoginForm;

public interface UserRepository extends MongoRepository<User, String> {
    public User findByToken(String token);
    public User findByLogin(LoginForm login);
    public User findByUsername(String username);
    public List<User> findAll();
}
