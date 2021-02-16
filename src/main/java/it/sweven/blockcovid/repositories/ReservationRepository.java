package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.MongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.Reservation;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findAllByUser(String user);
    Reservation findByIdAndUser(String id, String user);
    Reservation deleteByIdAndUser(String id, String user);
}
