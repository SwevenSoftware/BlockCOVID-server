package it.sweven.blockcovid.routers;

/* Spring Annotations */

import it.sweven.blockcovid.entities.Reservation;
import it.sweven.blockcovid.repositories.ReservationRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import it.sweven.blockcovid.services.UserRegistrationService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ReservationRouter {
  @Autowired private final ReservationRepository repository;
  @Autowired private UserAuthenticationService authenticationService;
  @Autowired private UserRegistrationService registrationService;

  public ReservationRouter(ReservationRepository repository) {
    this.repository = repository;
  }

  @PostMapping(value = "/user/reservations")
  Map<String, Reservation> getPersonalReservations(@RequestHeader String Authorization) {
    String username = authenticationService.authenticateByToken(Authorization).getUsername();
    return repository
        .findAllByUser(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .stream()
        .parallel()
        .collect(Collectors.toMap(Reservation::getId, Function.identity()));
  }

  @PostMapping("/user/reservations/{id}")
  Reservation getSingleReservation(@PathVariable String id, @RequestHeader String Authorization) {
    String username = authenticationService.authenticateByToken(Authorization).getUsername();
    return repository
        .findByIdAndUser(id, username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/user/reservations/{id}")
  String cancelReservation(@PathVariable String id, @RequestHeader String Authorization) {
    String username = authenticationService.authenticateByToken(Authorization).getUsername();
    return repository
        .deleteByIdAndUser(id, username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        .getId();
  }

  @PutMapping("/user/reservations/{id}")
  Reservation updateReservation(
      @PathVariable String id,
      @RequestHeader String Authorization,
      @RequestParam Optional<String> nameRoom,
      @RequestParam Optional<Integer> idDesk,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) Optional<LocalTime> from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) Optional<LocalTime> to) {
    String username = authenticationService.authenticateByToken(Authorization).getUsername();
    Reservation reservation =
        repository
            .findByIdAndUser(id, username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    nameRoom.ifPresent(reservation::setNameRoom);
    idDesk.ifPresent(reservation::setIdDesk);
    try {
      date.ifPresent(reservation::setDate);
      if (from.isPresent() && to.isPresent()) reservation.setTime(from.get(), to.get());
      else {
        from.ifPresent(reservation::setFrom);
        to.ifPresent(reservation::setTo);
      }
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    return repository.save(reservation);
  }

  @PostMapping("/rooms/{nameRoom}/reservations")
  List<Reservation> roomReservations(
      @PathVariable String nameRoom,
      @RequestParam String Authorization,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date) {
    authenticationService.authenticateByToken(Authorization);
    return repository
        .findByNameRoomAndDate(nameRoom, date.orElse(LocalDate.now()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/rooms/{nameRoom}/reserve")
  Reservation reserveRoom(
      @PathVariable String nameRoom,
      @RequestParam Integer idDesk,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime from,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime to,
      @RequestHeader String Authorization) {
    String username = authenticationService.authenticateByToken(Authorization).getUsername();
    Reservation toSave;
    try {
      toSave = new Reservation(nameRoom, idDesk, date, from, to, username);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    boolean conflict = repository.findAll().stream().parallel().anyMatch(r -> r.conflicts(toSave));
    if (conflict) throw new ResponseStatusException(HttpStatus.CONFLICT);
    else return repository.save(toSave);
  }
}
