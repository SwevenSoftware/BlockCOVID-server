package it.sweven.blockcovid.reservations.repositories;

import it.sweven.blockcovid.reservations.entities.Reservation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, Long> {
  Optional<Reservation> findReservationById(String id);

  Optional<Reservation> deleteReservationById(String id);

  Stream<Reservation> findReservationsByDeskIdAndStartIsAfter(String deskId, LocalDateTime start);

  Optional<Reservation> findReservationByDeskIdAndStartIsLessThanEqualAndEndIsGreaterThanEqual(
      String deskId, LocalDateTime start, LocalDateTime end);

  ArrayList<Reservation> findReservationsByUsernameAndStartIsGreaterThanEqual(
      String username, LocalDateTime start);

  Stream<Reservation> findReservationByStartIsGreaterThanEqual(LocalDateTime start);

  Stream<Reservation> findReservationsByDeskId(String deskId);
}
