package it.sweven.blockcovid.repositories;

/* Java utilities */
import java.time.LocalDate;
import java.util.List;

/* Spring imports */
import org.springframework.data.mongodb.repository.MongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findAllByUser(String user);
    Reservation findByIdAndUser(String id, String user);
    Reservation deleteByIdAndUser(String id, String user);
    List<Reservation> findByNameRoomAndDate(String nameRoom, LocalDate date);
}
