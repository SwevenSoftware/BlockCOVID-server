package it.sweven.blockcovid.reservations.servicies;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
  private final ReservationRepository reservationRepository;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository) {
    this.reservationRepository = reservationRepository;
  }

  public Reservation addReservation(ReservationInfo reservationInfo, String username)
      throws ReservationClash {
    return save(
        new Reservation(
            reservationInfo.getDeskId(),
            username,
            reservationInfo.getStart(),
            reservationInfo.getEnd()));
  }

  public Optional<Reservation> findIfTimeFallsInto(String deskId, LocalDateTime timestamp) {
    return reservationRepository.findReservationsByDeskIdAndStartIsBeforeAndEndIsAfter(
        deskId, timestamp, timestamp);
  }

  public Reservation findById(String id) throws NoSuchReservation {
    return reservationRepository.findReservationById(id).orElseThrow(NoSuchReservation::new);
  }

  public Reservation save(Reservation reservation) throws ReservationClash {
    if (reservationConflict(
        new ReservationInfo(reservation.getDeskId(), reservation.getStart(), reservation.getEnd())))
      throw new ReservationClash();
    return reservationRepository.save(reservation);
  }

  public Reservation delete(String id) throws NoSuchReservation {
    return reservationRepository.deleteReservationById(id).orElseThrow(NoSuchReservation::new);
  }

  public Optional<Reservation> nextReservation(String deskId, LocalDateTime timestamp) {
    return reservationRepository
        .findReservationsByDeskIdAndStartIsAfter(deskId, timestamp)
        .sorted()
        .findFirst();
  }

  private boolean reservationConflict(ReservationInfo reservationInfo) {
    return reservationRepository
            .findReservationsByDeskIdAndStartIsAfter(
                reservationInfo.getDeskId(), reservationInfo.getStart())
            .parallel()
            .anyMatch(reservation -> reservationInfo.getEnd().isAfter(reservation.getStart()))
        || reservationRepository
            .findReservationsByDeskIdAndEndIsBefore(
                reservationInfo.getDeskId(), reservationInfo.getEnd())
            .parallel()
            .anyMatch(reservation -> reservationInfo.getStart().isBefore(reservation.getEnd()));
  }

  public List<Reservation> findByUsernameAndStart(String username, LocalDateTime start) {
    List<Reservation> allFutureReservations =
        reservationRepository.findReservationsByUsernameAndStartIsAfter(username, start);
    reservationRepository
        .findReservationByUsernameAndStartIsBeforeAndEndIsAfter(username, start, start)
        .ifPresent(reservation -> allFutureReservations.add(0, reservation));
    return allFutureReservations;
  }

  public List<Reservation> findByTimeInterval(LocalDateTime start, LocalDateTime end) {
    return reservationRepository
        .findReservationByStartIsGreaterThanEqual(start)
        .filter(r -> !r.getStart().isAfter(end))
        .collect(Collectors.toList());
  }
}
