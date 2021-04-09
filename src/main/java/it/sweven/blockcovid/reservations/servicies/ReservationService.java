package it.sweven.blockcovid.reservations.servicies;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
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
    if (reservationRepository
            .findReservationsByDeskIdAndStartIsAfter(
                reservationInfo.getDeskId(), reservationInfo.getStart())
            .parallel()
            .anyMatch(reservation -> reservationInfo.getEnd().isAfter(reservation.getStart()))
        || reservationRepository
            .findReservationsByDeskIdAndEndIsBefore(
                reservationInfo.getDeskId(), reservationInfo.getEnd())
            .parallel()
            .anyMatch(reservation -> reservationInfo.getStart().isBefore(reservation.getEnd())))
      throw new ReservationClash();
    return reservationRepository.save(
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

  public Optional<Reservation> nextReservation(String deskId, LocalDateTime timestamp) {
    return reservationRepository
        .findReservationsByDeskIdAndStartIsAfter(deskId, timestamp)
        .sorted()
        .findFirst();
  }
}
