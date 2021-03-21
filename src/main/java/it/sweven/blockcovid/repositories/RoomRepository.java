package it.sweven.blockcovid.repositories;

import it.sweven.blockcovid.entities.room.Room;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
  Optional<Room> findRoomByName(String name);
}
