package it.sweven.blockcovid.users.repositories;

/* Java imports */

import it.sweven.blockcovid.users.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  List<User> findAll();

  Optional<User> deleteUserByUsername(String username);
}
