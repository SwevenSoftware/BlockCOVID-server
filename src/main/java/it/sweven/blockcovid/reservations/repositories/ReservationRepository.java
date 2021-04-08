package it.sweven.blockcovid.reservations.repositories;

import it.sweven.blockcovid.reservations.entities.Reservation;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, Long> {
  Stream<Reservation> findReservationsByDeskIdAndStartIsAfter(String deskId, LocalDateTime start);

  Stream<Reservation> findReservationsByDeskIdAndEndIsBefore(String deskId, LocalDateTime end);
}
