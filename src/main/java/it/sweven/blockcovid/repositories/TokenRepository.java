package it.sweven.blockcovid.repositories;

import it.sweven.blockcovid.entities.user.Token;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, String> {
  Optional<Token> findById(String id);

  void deleteById(String id);
}
