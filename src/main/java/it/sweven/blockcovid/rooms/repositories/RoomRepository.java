package it.sweven.blockcovid.rooms.repositories;

import it.sweven.blockcovid.rooms.entities.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
  Optional<Room> findRoomByName(String name);

  List<Room> findAll();

  Optional<Room> deleteRoomByName(String name);

  Optional<Room> findByName(String name);
}
