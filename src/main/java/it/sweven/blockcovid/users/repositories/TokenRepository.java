package it.sweven.blockcovid.users.repositories;

import it.sweven.blockcovid.users.entities.Token;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
  Optional<Token> findById(String id);

  void deleteById(String id);
}
