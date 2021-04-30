package it.sweven.blockcovid.reservations.servicies;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.entities.ReservationBuilder;
import it.sweven.blockcovid.reservations.exceptions.*;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.management.BadAttributeValueExpException;
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

  public ReservationWithRoom addReservation(ReservationInfo reservationInfo, String username)
      throws ReservationClash, BadTimeIntervals, DeskNotFoundException, RoomNotFoundException,
          BadAttributeValueExpException {
    Desk toBook =
        deskRepository
            .findById(reservationInfo.getDeskId())
            .orElseThrow(DeskNotFoundException::new);
    Room booked =
        roomRepository.findById(toBook.getRoomId()).orElseThrow(RoomNotFoundException::new);
    if (!booked.isRoomOpen(reservationInfo.getStart())
        || !booked.isRoomOpen(reservationInfo.getEnd())) throw new BadTimeIntervals();

    LocalDateTime nextClosing = reservationInfo.getStart().with(booked.getClosingTime());
    if (reservationInfo.getEnd().isAfter(nextClosing)) throw new BadTimeIntervals();

    return save(
        new ReservationBuilder()
            .deskId(reservationInfo.getDeskId())
            .username(username)
            .start(reservationInfo.getStart())
            .end(reservationInfo.getEnd())
            .build());
  }

  public Optional<ReservationWithRoom> findIfTimeFallsInto(String deskId, LocalDateTime timestamp) {
    return reservationRepository
        .findReservationByDeskIdAndStartIsLessThanEqualAndEndIsGreaterThanEqual(
            deskId, timestamp, timestamp)
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null);
  }

  public ReservationWithRoom findById(String id) throws NoSuchReservation {
    return reservationRepository
        .findReservationById(id)
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null)
        .orElseThrow(NoSuchReservation::new);
  }

  public ReservationWithRoom save(Reservation reservation)
      throws ReservationClash, RoomNotFoundException {
    if (reservationConflict(reservation)) throw new ReservationClash();
    Reservation saved = reservationRepository.save(reservation);
    String roomName = getRoomName(saved.getDeskId());
    if (roomName == null) throw new RoomNotFoundException();
    return new ReservationWithRoom(
        saved.getId(),
        saved.getDeskId(),
        roomName,
        saved.getUsername(),
        saved.getStart(),
        saved.getEnd(),
        saved.getRealStart(),
        saved.getRealEnd(),
        saved.getDeskCleaned());
  }

  public ReservationWithRoom delete(String id) throws NoSuchReservation {
    return reservationRepository
        .deleteReservationById(id)
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null)
        .orElseThrow(NoSuchReservation::new);
  }

  public Optional<ReservationWithRoom> nextReservation(String deskId, LocalDateTime timestamp) {
    return reservationRepository
        .findReservationsByDeskIdAndStartIsAfter(deskId, timestamp)
        .sorted()
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null)
        .findFirst();
  }

  private boolean reservationConflict(Reservation reservation) {
    return reservationRepository
        .findReservationsByDeskId(reservation.getDeskId())
        .parallel()
        .anyMatch(reservation1 -> reservation1.clashesWith(reservation));
  }

  public boolean timeConflict(String deskId, LocalDateTime from, LocalDateTime to) {
    return reservationRepository
        .findReservationsByDeskId(deskId)
        .anyMatch(reservation -> reservation.intervalInsideReservation(from, to));
  }

  public List<ReservationWithRoom> findByUsernameAndEnd(String username, LocalDateTime end) {
    return reservationRepository
        .findReservationsByUsernameAndEndIsGreaterThanEqual(username, end)
        .stream()
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null)
        .collect(Collectors.toList());
  }

  public List<ReservationWithRoom> findByTimeInterval(LocalDateTime start, LocalDateTime end) {
    return reservationRepository
        .findReservationByStartIsGreaterThanEqual(start)
        .filter(r -> !r.getStart().isAfter(end))
        .map(
            r ->
                new ReservationWithRoom(
                    r.getId(),
                    r.getDeskId(),
                    getRoomName(r.getDeskId()),
                    r.getUsername(),
                    r.getStart(),
                    r.getEnd(),
                    r.getRealStart(),
                    r.getRealEnd(),
                    r.getDeskCleaned()))
        .filter(r -> r.getRoom() != null)
        .collect(Collectors.toList());
  }

  private String getRoomName(String deskId) {
    Optional<Desk> desk = deskRepository.findById(deskId);
    if (desk.isEmpty()) return null;
    Optional<Room> room = roomRepository.findById(desk.get().getRoomId());
    if (room.isEmpty()) return null;
    return room.get().getName();
  }

  public ReservationWithRoom start(String id, LocalDateTime time)
      throws NoSuchReservation, StartingTooEarly, ReservationClash {
    Reservation toStart =
        reservationRepository.findReservationById(id).orElseThrow(NoSuchReservation::new);
    if (time.isBefore(toStart.getStart().minusMinutes(30))) throw new StartingTooEarly();
    toStart.setRealStart(time);
    ReservationWithRoom toReturn = save(toStart);
    deskRepository
        .findById(toReturn.getDeskId())
        .ifPresent(
            desk -> {
              desk.setDeskStatus(Status.DIRTY);
              deskRepository.save(desk);
            });
    return toReturn;
  }

  public ReservationWithRoom end(String id, LocalDateTime time, Boolean deskCleaned)
      throws BadTimeIntervals, NoSuchReservation, AlreadyEnded, ReservationClash {
    Reservation toEnd =
        reservationRepository.findReservationById(id).orElseThrow(NoSuchReservation::new);
    if (toEnd.getRealEnd() != null) throw new AlreadyEnded();
    if (toEnd.getRealStart() == null || toEnd.getRealStart().isAfter(time))
      throw new BadTimeIntervals();
    toEnd.setRealEnd(time);
    toEnd.setDeskCleaned(deskCleaned);
    if (deskCleaned)
      deskRepository
          .findById(toEnd.getDeskId())
          .ifPresent(
              d -> {
                d.setDeskStatus(Status.CLEAN);
                deskRepository.save(d);
              });
    return save(toEnd);
  }
}
