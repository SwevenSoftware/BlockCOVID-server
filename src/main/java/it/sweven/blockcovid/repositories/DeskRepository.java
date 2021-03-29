package it.sweven.blockcovid.repositories;

import it.sweven.blockcovid.entities.room.Desk;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeskRepository extends MongoRepository<Desk, Long> {
  Optional<Desk> findByXAndYAndRoomId(Integer X, Integer Y, String roomId);

  List<Desk> findAllByRoomId(String roomId);

  List<Desk> deleteAllByRoomId(String roomId);

  Optional<Desk> deleteByXAndYAndRoomId(Integer X, Integer Y, String roomId);
}
