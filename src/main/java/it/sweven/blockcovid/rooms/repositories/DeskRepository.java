package it.sweven.blockcovid.rooms.repositories;

import it.sweven.blockcovid.rooms.entities.Desk;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeskRepository extends MongoRepository<Desk, Long> {
  Optional<Desk> findByXAndYAndRoomId(Integer X, Integer Y, String roomId);

  List<Desk> findAllByRoomId(String roomId);

  Optional<Desk> findById(String id);

  List<Desk> deleteAllByRoomId(String roomId);

  Optional<Desk> getByXAndYAndRoomId(Integer X, Integer Y, String roomId);

  Optional<Desk> deleteById(String id);
}
