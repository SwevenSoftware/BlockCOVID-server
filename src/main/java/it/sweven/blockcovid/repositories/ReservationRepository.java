package it.sweven.blockcovid.repositories;

/* Java utilities */

import it.sweven.blockcovid.entities.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
  Optional<List<Reservation>> findAllByUser(String user);

  Optional<Reservation> findByIdAndUser(String id, String user);

  Optional<Reservation> deleteByIdAndUser(String id, String user);

  Optional<List<Reservation>> findByNameRoomAndDate(String nameRoom, LocalDate date);
}
