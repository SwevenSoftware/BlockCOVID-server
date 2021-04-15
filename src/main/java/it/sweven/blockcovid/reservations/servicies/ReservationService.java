package it.sweven.blockcovid.reservations.servicies;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final RoomRepository roomRepository;
  private final DeskRepository deskRepository;

  @Autowired
  public ReservationService(
      ReservationRepository reservationRepository,
      RoomRepository roomRepository,
      DeskRepository deskRepository) {
    this.reservationRepository = reservationRepository;
    this.roomRepository = roomRepository;
    this.deskRepository = deskRepository;
  }

  public Reservation addReservation(ReservationInfo reservationInfo, String username)
      throws ReservationClash, BadTimeIntervals, DeskNotFoundException, RoomNotFoundException {
    Desk toBook =
        deskRepository
            .findById(reservationInfo.getDeskId())
            .orElseThrow(DeskNotFoundException::new);
    Room booked =
        roomRepository.findById(toBook.getRoomId()).orElseThrow(RoomNotFoundException::new);
    if (booked.getOpeningTime().isAfter(reservationInfo.getStart().toLocalTime())
        || booked.getClosingTime().isBefore(reservationInfo.getEnd().toLocalTime()))
      throw new BadTimeIntervals();
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
    if (reservationConflict(reservation)) throw new ReservationClash();
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

  private boolean reservationConflict(Reservation reservation) {
    return reservationRepository
            .findReservationsByDeskIdAndStartIsAfter(
                reservation.getDeskId(), reservation.getStart())
            .parallel()
            .anyMatch(foundReservation -> reservation.getEnd().isAfter(foundReservation.getStart()))
        || reservationRepository
            .findReservationsByDeskIdAndEndIsBefore(reservation.getDeskId(), reservation.getEnd())
            .parallel()
            .anyMatch(
                foundReservation -> reservation.getStart().isBefore(foundReservation.getEnd()));
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
