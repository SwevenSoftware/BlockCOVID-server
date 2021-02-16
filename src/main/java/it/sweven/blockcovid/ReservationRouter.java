package it.sweven.blockcovid;

/* Spring Annotations */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

/* Bean factory annotations */
import org.springframework.beans.factory.annotation.Autowired;

/* Reactor core imports */
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/* Our own imports */
import it.sweven.blockcovid.entities.*;
import it.sweven.blockcovid.repositories.ReservationRepository;

import java.util.Map;
import java.util.Optional;

@RestController
public class ReservationRouter {
    @Autowired
    private final ReservationRepository repository;

    public ReservationRouter(ReservationRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value="/user/reservations", params={"nameRoom", "idDesk", "from", "to", "tokenAuth"})
    Mono<Reservation> newReservation(@RequestParam String nameRoom, @RequestParam Integer idDesk,
                                     @RequestParam String from, @RequestParam String to,
                                     @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Reservation toSave = new Reservation(nameRoom, idDesk, from, to, user);
        boolean conflict = repository.findAll()
                                .filter(r -> r.conflicts(toSave))
                                .hasElements().blockOptional()
                                .orElseThrow(() ->
                                        new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));
        if(conflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else
            return repository.save(toSave);
    }

    @PostMapping(value="/user/reservations", params="tokenAuth")
    Mono<Map<String, Reservation>> getPersonalReservations(@RequestParam String tokenAuth) {
        return repository.findAllByUser(tokenAuth).collectMap(Reservation::getId);
    }

    @PostMapping("/user/reservations/{id}")
    Mono<Reservation> getSingleReservation(@PathVariable String id, @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.findByIdAndUser(id, user);
    }

    @DeleteMapping("/user/reservations/{id}")
    Mono<String> cancelReservation(@PathVariable String id, @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.deleteByIdAndUser(id, user).map(Reservation::getId);
    }

    @PutMapping("/user/reservations/{id}")
    Mono<Reservation> updateReservation(@PathVariable String id, @RequestParam String tokenAuth,
                            @RequestParam Optional<String> nameRoom, @RequestParam Optional<Integer> idDesk,
                            @RequestParam Optional<String> from, @RequestParam Optional<String> to) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Mono<Reservation> reservation = repository.findByIdAndUser(id, user);
        if(nameRoom.isPresent())
            reservation = reservation.map(r -> { r.setNameRoom(nameRoom.get()); return r; });
        if(idDesk.isPresent())
            reservation = reservation.map(r -> { r.setIdDesk(idDesk.get()); return r; });
        if(from.isPresent())
            reservation = reservation.map(r -> { r.setFrom(from.get()); return r; });
        if(to.isPresent())
            reservation = reservation.map(r -> { r.setTo(to.get()); return r; });
        return reservation.flatMap(repository::save);
    }
}
