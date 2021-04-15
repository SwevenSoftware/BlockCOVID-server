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

  Stream<Reservation> findReservationsByDeskIdAndEndIsBefore(String deskId, LocalDateTime end);

  Optional<Reservation> findReservationsByDeskIdAndStartIsBeforeAndEndIsAfter(
      String deskId, LocalDateTime start, LocalDateTime end);

  ArrayList<Reservation> findReservationsByUsernameAndStartIsAfter(
      String username, LocalDateTime start);

  Optional<Reservation> findReservationByUsernameAndStartIsBeforeAndEndIsAfter(
      String username, LocalDateTime start, LocalDateTime end);

  Stream<Reservation> findReservationByStartIsGreaterThanEqual(LocalDateTime start);
}
