package it.sweven.blockcovid;

/* Spring Annotations */
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @PostMapping(value="/user/reservations")
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
                                  @RequestParam Optional<String> nameRoom,
                                  @RequestParam Optional<Integer> idDesk,
                                  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                                          Optional<LocalDate> date,
                                  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME)
                                          Optional<LocalTime> from,
                                  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME)
                                          Optional<LocalTime> to) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Reservation reservation = repository.findByIdAndUser(id, user);
        nameRoom.ifPresent(reservation::setNameRoom);
        idDesk.ifPresent(reservation::setIdDesk);
        date.ifPresent(reservation::setDate);
        try {
            if (from.isPresent() && to.isPresent())
                reservation.setTime(from.get(), to.get());
            else {
                from.ifPresent(reservation::setFrom);
                to.ifPresent(reservation::setTo);
            }
        } catch( IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return repository.save(reservation);
    }

    @PostMapping("/rooms/{nameRoom}/reservations")
    List<Reservation> roomReservations(@PathVariable String nameRoom,
                                       @RequestParam String tokenAuth,
                                       @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
                                               Optional<LocalDate> date) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return repository.findByNameRoomAndDate(nameRoom, date.orElse(LocalDate.now()));
    }

    @PostMapping("/rooms/{nameRoom}/reserve")
    Reservation reserveRoom(@PathVariable String nameRoom, @RequestParam Integer idDesk,
                            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,
                            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime from,
                            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.TIME) LocalTime to,
                            @RequestParam String tokenAuth) {
        String user = tokenAuth;  // TODO: placeholder until User.checkToken() is implemented
        if(user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        Reservation toSave;
        try {
            toSave = new Reservation(nameRoom, idDesk, date, from, to, user);
        } catch( IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        boolean conflict = repository.findAll().stream().parallel()
                .anyMatch(r -> r.conflicts(toSave));
        if(conflict)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        else
            return repository.save(toSave);
    }

}