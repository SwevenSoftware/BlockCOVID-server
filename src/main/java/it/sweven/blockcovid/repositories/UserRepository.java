package it.sweven.blockcovid.repositories;

/* Java imports */

import it.sweven.blockcovid.entities.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  public Optional<User> findByToken_Token(String token);

  public Optional<User> findByUsername(String username);

  public List<User> findAll();
}
