package it.sweven.blockcovid.repositories;

/* Spring imports */
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/* Our imports */
import it.sweven.blockcovid.entities.Reservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
    Flux<Reservation> findAllByUser(String user);
    Mono<Reservation> findByIdAndUser(String id, String user);
    Mono<Reservation> deleteByIdAndUser(String id, String user);
}
