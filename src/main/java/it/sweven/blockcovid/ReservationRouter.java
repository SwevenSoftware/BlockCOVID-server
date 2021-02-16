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

/* Our own imports */
import it.sweven.blockcovid.entities.Reservation;
import it.sweven.blockcovid.repositories.ReservationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class ReservationRouter {
    @Autowired
    private final ReservationRepository repository;

    public ReservationRouter(ReservationRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value="/user/reservations", params={"nameRoom", "idDesk", "from", "to", "tokenAuth"})
    Reservation newReservation(@RequestParam String nameRoom, @RequestParam Integer idDesk,
                               @RequestParam String from, @RequestParam String to,
                               @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Reservation toSave = new Reservation(nameRoom, idDesk, from, to, user);
        boolean conflict = repository.findAll().stream().parallel()
                                     .anyMatch(r -> r.conflicts(toSave));
        if(conflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else
            return repository.save(toSave);
    }

    @PostMapping(value="/user/reservations", params="tokenAuth")
    Map<String, Reservation> getPersonalReservations(@RequestParam String tokenAuth) {
        return repository.findAllByUser(tokenAuth)
                .stream().parallel()
                .collect(Collectors.toMap(Reservation::getId, Function.identity()));
    }

    @PostMapping("/user/reservations/{id}")
    Reservation getSingleReservation(@PathVariable String id, @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.findByIdAndUser(id, user);
    }

    @DeleteMapping("/user/reservations/{id}")
    String cancelReservation(@PathVariable String id, @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.deleteByIdAndUser(id, user).getId();
    }

    @PutMapping("/user/reservations/{id}")
    Reservation updateReservation(@PathVariable String id, @RequestParam String tokenAuth,
                                  @RequestParam Optional<String> nameRoom, @RequestParam Optional<Integer> idDesk,
                                  @RequestParam Optional<String> from, @RequestParam Optional<String> to) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Reservation reservation = repository.findByIdAndUser(id, user);
        nameRoom.ifPresent(reservation::setNameRoom);
        idDesk.ifPresent(reservation::setIdDesk);
        from.ifPresent(reservation::setFrom);
        to.ifPresent(reservation::setTo);
        return repository.save(reservation);
    }
}