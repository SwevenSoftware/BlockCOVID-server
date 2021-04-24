package it.sweven.blockcovid.reservations.servicies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.BadTimeIntervals;
import it.sweven.blockcovid.reservations.exceptions.NoSuchReservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.exceptions.StartingTooEarly;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import it.sweven.blockcovid.rooms.entities.Desk;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.exceptions.DeskNotFoundException;
import it.sweven.blockcovid.rooms.exceptions.RoomNotFoundException;
import it.sweven.blockcovid.rooms.repositories.DeskRepository;
import it.sweven.blockcovid.rooms.repositories.RoomRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {
  private ReservationRepository reservationRepository;
  private RoomRepository roomRepository;
  private DeskRepository deskRepository;
  private ReservationService service;

  private ReservationInfo info;
  private Reservation fakeReservation1, fakeReservation2, fakeReservation3, fakeReservation4;
  private final String username = "username";
  private Room fakeRoom;

  @BeforeEach
  void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    fakeRoom = mock(Room.class);
    when(fakeRoom.getName()).thenReturn("roomName");
    when(fakeRoom.isRoomOpen(any())).thenReturn(true);
    when(fakeRoom.getClosingTime()).thenReturn(LocalTime.now().withHour(23));

    roomRepository = mock(RoomRepository.class);
    when(roomRepository.findById(any())).thenReturn(Optional.of(fakeRoom));

    deskRepository = mock(DeskRepository.class);
    when(deskRepository.findById(anyString())).thenReturn(Optional.of(mock(Desk.class)));

    Desk fetchedDesk = mock(Desk.class);
    when(fetchedDesk.getRoomId()).thenReturn("roomId");
    when(deskRepository.findById(anyString())).thenReturn(Optional.of(fetchedDesk));

    service = new ReservationService(reservationRepository, roomRepository, deskRepository);
    doAnswer(invocation -> invocation.getArgument(0)).when(reservationRepository).save(any());

    info = mock(ReservationInfo.class);
    when(info.getDeskId()).thenReturn("desk");
    when(info.getStart()).thenReturn(LocalDateTime.now().withHour(17));
    when(info.getEnd()).thenReturn(LocalDateTime.now().withHour(18));

    fakeReservation1 = mock(Reservation.class);
    fakeReservation2 = mock(Reservation.class);
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().withHour(20));
    when(fakeReservation1.getEnd()).thenReturn(LocalDateTime.now().withHour(22));
    when(fakeReservation2.getStart()).thenReturn(LocalDateTime.now().withHour(22));
    when(fakeReservation2.getEnd()).thenReturn(LocalDateTime.now().withHour(23));

    fakeReservation3 = mock(Reservation.class);
    fakeReservation4 = mock(Reservation.class);
    when(fakeReservation3.getStart()).thenReturn(LocalDateTime.now().withHour(10));
    when(fakeReservation3.getEnd()).thenReturn(LocalDateTime.now().withHour(12));
    when(fakeReservation4.getStart()).thenReturn(LocalDateTime.now().withHour(12));
    when(fakeReservation4.getEnd()).thenReturn(LocalDateTime.now().withHour(13));

    when(reservationRepository.findReservationsByDeskId(any()))
        .thenReturn(
            Stream.of(fakeReservation1, fakeReservation2, fakeReservation3, fakeReservation4));
  }

  private void reservationsEquals(
      Reservation reservation, ReservationWithRoom reservationWithRoom) {
    assertEquals(reservation.getId(), reservationWithRoom.getId());
    assertEquals(reservation.getDeskId(), reservationWithRoom.getDeskId());
    assertEquals(reservation.getUsername(), reservationWithRoom.getUsername());
    assertEquals(reservation.getStart(), reservationWithRoom.getStart());
    assertEquals(reservation.getEnd(), reservationWithRoom.getEnd());
  }

  private Reservation getReservationMock(
      String id, String deskId, String username, LocalDateTime start, LocalDateTime end) {
    Reservation reservation = mock(Reservation.class);
    when(reservation.getId()).thenReturn(id);
    when(reservation.getDeskId()).thenReturn(deskId);
    when(reservation.getUsername()).thenReturn(username);
    when(reservation.getStart()).thenReturn(start);
    when(reservation.getEnd()).thenReturn(end);
    return reservation;
  }

  @Test
  void validAddition() throws ReservationClash, BadTimeIntervals, BadAttributeValueExpException {
    ReservationWithRoom added = service.addReservation(info, username);
    assertEquals(info.getDeskId(), added.getDeskId());
    assertEquals(info.getStart(), added.getStart());
    assertEquals(info.getEnd(), added.getEnd());
    assertEquals(username, added.getUsername());
  }

  @Test
  void clashesAreReported() {
    when(fakeReservation1.clashesWith(any())).thenReturn(true);
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }

  @Test
  void findIfTimeFallsInto_callsRepository() {
    Reservation reservation =
        getReservationMock(
            "reservationId",
            "deskId",
            "username",
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusMinutes(30));
    when(reservationRepository
            .findReservationByDeskIdAndStartIsLessThanEqualAndEndIsGreaterThanEqual(
                any(), any(), any()))
        .thenReturn(Optional.of(reservation));
    LocalDateTime fakeTime = LocalDateTime.MIN;
    reservationsEquals(reservation, service.findIfTimeFallsInto("id", fakeTime).get());
  }

  @Test
  void nextReservationSortsAccordingToReservationComparable() {
    Reservation reservation1 =
        getReservationMock(
            "reservationId1",
            "deskId1",
            "username",
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusMinutes(30));
    Reservation reservation2 =
        getReservationMock(
            "reservationId1",
            "deskId1",
            "username",
            LocalDateTime.now().plusMinutes(70),
            LocalDateTime.now().plusMinutes(90));
    when(reservationRepository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenReturn(Stream.of(reservation2, reservation1));
    reservationsEquals(reservation2, service.nextReservation("id", LocalDateTime.MIN).get());
    verify(reservationRepository).findReservationsByDeskIdAndStartIsAfter("id", LocalDateTime.MIN);
  }

  @Test
  void findById() {
    Reservation reservation =
        getReservationMock(
            "reservationId1",
            "deskId1",
            "username",
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusMinutes(30));
    when(reservationRepository.findReservationById("idReservation"))
        .thenReturn(Optional.of(reservation));
    reservationsEquals(reservation, service.findById("idReservation"));
  }

  @Test
  void delete_validId() {
    Reservation reservation =
        getReservationMock(
            "reservationId1",
            "deskId1",
            "username",
            LocalDateTime.now().plusMinutes(10),
            LocalDateTime.now().plusMinutes(30));
    when(reservationRepository.deleteReservationById("idReservation"))
        .thenReturn(Optional.of(reservation));
    reservationsEquals(reservation, service.delete("idReservation"));
  }

  @Test
  void delete_idNotFound_throwsNoSuchReservation() {
    when(reservationRepository.deleteReservationById(anyString())).thenReturn(Optional.empty());
    assertThrows(NoSuchReservation.class, () -> service.delete("idReservation"));
  }

  @Test
  void findByUsernameAndStart_reservationsFound() {
    List<Reservation> expectedList =
        List.of(
            getReservationMock(
                "reservationId1",
                "deskId1",
                "username",
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(30)),
            getReservationMock(
                "reservationId2",
                "deskId2",
                "username",
                LocalDateTime.now().plusMinutes(40),
                LocalDateTime.now().plusMinutes(60)));
    when(reservationRepository.findReservationsByUsernameAndStartIsGreaterThanEqual(
            anyString(), any()))
        .thenReturn(new ArrayList<>(expectedList));
    List<ReservationWithRoom> actualList =
        service.findByUsernameAndStart("user", LocalDateTime.now());
    assertEquals(expectedList.size(), actualList.size());
    for (int i = 0; i < actualList.size(); i++) {
      reservationsEquals(expectedList.get(i), actualList.get(i));
    }
  }

  @Test
  void findByUsernameAndStart_noReservationsFound() {
    when(reservationRepository.findReservationsByUsernameAndStartIsGreaterThanEqual(
            anyString(), any()))
        .thenReturn(new ArrayList<>());
    assertEquals(
        Collections.emptyList(), service.findByUsernameAndStart("user", LocalDateTime.now()));
  }

  @Test
  void findByTimeInterval_startBeforeEnd() {
    LocalDateTime providedStart = LocalDateTime.now().plusMinutes(20),
        providedEnd = LocalDateTime.now().plusMinutes(40);
    Reservation
        reservation1 =
            getReservationMock(
                "reservationId1",
                "deskId1",
                "username",
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(30)),
        reservation2 =
            getReservationMock(
                "reservationId2",
                "deskId2",
                "username",
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now().plusMinutes(50)),
        reservation3 =
            getReservationMock(
                "reservationId3",
                "deskId3",
                "username",
                LocalDateTime.now().plusMinutes(60),
                LocalDateTime.now().plusMinutes(90));
    when(reservationRepository.findReservationByStartIsGreaterThanEqual(providedStart))
        .thenReturn(Stream.of(reservation1, reservation2, reservation3));
    List<Reservation> expectedList = List.of(reservation1, reservation2);
    List<ReservationWithRoom> actualList = service.findByTimeInterval(providedStart, providedEnd);
    assertEquals(expectedList.size(), actualList.size());
    for (int i = 0; i < actualList.size(); i++) {
      reservationsEquals(expectedList.get(i), actualList.get(i));
    }
  }

  @Test
  void findByTimeInterval_startAfterEnd_returnEmptyList() {
    LocalDateTime providedStart = LocalDateTime.now().plusMinutes(20),
        providedEnd = LocalDateTime.now().plusMinutes(10);
    when(fakeReservation1.getStart()).thenReturn(providedStart);
    when(fakeReservation2.getStart()).thenReturn(providedStart.plusMinutes(20));
    when(fakeReservation3.getStart()).thenReturn(providedStart.plusMinutes(40));
    when(reservationRepository.findReservationByStartIsGreaterThanEqual(providedStart))
        .thenReturn(Stream.of(fakeReservation1, fakeReservation2, fakeReservation3));
    assertEquals(Collections.emptyList(), service.findByTimeInterval(providedStart, providedEnd));
  }

  @Test
  void findByTimeInterval_startEqualsEnd() {
    LocalDateTime providedStart = LocalDateTime.now().plusMinutes(20),
        providedEnd = LocalDateTime.now().plusMinutes(20);
    Reservation
        reservation1 =
            getReservationMock(
                "reservationId1",
                "deskId1",
                "username",
                providedStart,
                providedStart.plusMinutes(30)),
        reservation2 =
            getReservationMock(
                "reservationId2",
                "deskId2",
                "username",
                providedStart.plusMinutes(20),
                providedStart.plusMinutes(50));
    when(fakeReservation1.getStart()).thenReturn(providedStart);
    when(fakeReservation2.getStart()).thenReturn(providedStart.plusMinutes(20));
    when(reservationRepository.findReservationByStartIsGreaterThanEqual(providedStart))
        .thenReturn(Stream.of(reservation1, reservation2));
    reservationsEquals(reservation1, service.findByTimeInterval(providedStart, providedEnd).get(0));
  }

  @Test
  void deskNotFound_throwsDeskNotFoundException() {
    when(deskRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(DeskNotFoundException.class, () -> service.addReservation(info, username));
  }

  @Test
  void roomNotFound_throwsRoomNotFoundException() {
    when(roomRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(RoomNotFoundException.class, () -> service.addReservation(info, username));
  }

  @Test
  void reservationBeforeOpeningTime_throwsBadTimeInterval() {
    when(fakeRoom.isRoomOpen(info.getStart())).thenReturn(false);
    when(fakeRoom.isRoomOpen(info.getEnd())).thenReturn(true);
    assertThrows(BadTimeIntervals.class, () -> service.addReservation(info, username));
  }

  @Test
  void reservationAfterClosingTime_throwsBadTimeInterval() {
    when(fakeRoom.isRoomOpen(info.getStart())).thenReturn(true);
    when(fakeRoom.isRoomOpen(info.getEnd())).thenReturn(false);
    assertThrows(BadTimeIntervals.class, () -> service.addReservation(info, username));
  }

  @Test
  void addReservation_reservationEndsAfterClosingTime() {
    when(fakeRoom.getClosingTime()).thenReturn(LocalTime.now().withHour(13));
    assertThrows(BadTimeIntervals.class, () -> service.addReservation(info, username));
  }

  @Test
  void timeConflictFindsConflict() {
    when(fakeReservation1.intervalInsideReservation(any(), any())).thenReturn(true);
    assertTrue(
        service.timeConflict("id1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)));
  }

  @Test
  void timeConflictNoConflicts() {
    when(fakeReservation1.intervalInsideReservation(any(), any())).thenReturn(false);
    when(fakeReservation2.intervalInsideReservation(any(), any())).thenReturn(false);
    when(fakeReservation3.intervalInsideReservation(any(), any())).thenReturn(false);
    when(fakeReservation4.intervalInsideReservation(any(), any())).thenReturn(false);
    assertFalse(
        service.timeConflict("id1", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)));
  }

  @Test
  void startHappyPath() throws ReservationClash, StartingTooEarly {
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now());
    when(fakeReservation1.getDeskId()).thenReturn("");
    when(reservationRepository.findReservationById(any()))
        .thenReturn(Optional.of(fakeReservation1));
    LocalDateTime now = LocalDateTime.now();
    AtomicBoolean correctTime = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              correctTime.set(invocation.getArgument(0) == now);
              return null;
            })
        .when(fakeReservation1)
        .setRealStart(any());
    service.start("id", now);
    assertTrue(correctTime.get());
  }

  @Test
  void startReservationNotFound() {
    when(reservationRepository.findReservationById(any())).thenReturn(Optional.empty());
    LocalDateTime now = LocalDateTime.now();
    assertThrows(NoSuchReservation.class, () -> service.start("id", now));
  }

  @Test
  void startStartingBefore30Minutes() {
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now());
    when(fakeReservation1.getDeskId()).thenReturn("");
    when(reservationRepository.findReservationById(any()))
        .thenReturn(Optional.of(fakeReservation1));
    LocalDateTime now = LocalDateTime.now();
    assertThrows(StartingTooEarly.class, () -> service.start("id", now.minusMinutes(31)));
  }
}
