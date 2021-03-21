package it.sweven.blockcovid.repositories;

import it.sweven.blockcovid.entities.room.Desk;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeskRepository extends MongoRepository<Desk, Long> {
  Optional<Desk> findByIdAndRoomId(Integer Id, String roomId);

  Optional<Desk> findByXAndYAndRoomId(Integer X, Integer Y, String roomId);
}
