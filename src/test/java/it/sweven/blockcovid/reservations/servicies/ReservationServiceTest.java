package it.sweven.blockcovid.reservations.servicies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {
  private ReservationRepository reservationRepository;
  private RoomRepository roomRepository;
  private DeskRepository deskRepository;
  private ReservationService service;

  private ReservationInfo info;
  private Reservation fakeReservation1, fakeReservation2, fakeReservation3;
  private final String username = "username";
  private Room fakeRoom;

  @BeforeEach
  void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    fakeRoom = mock(Room.class);
    when(fakeRoom.isRoomOpen(any())).thenReturn(true);

    roomRepository = mock(RoomRepository.class);
    when(roomRepository.findById(any())).thenReturn(Optional.of(fakeRoom));

    deskRepository = mock(DeskRepository.class);
    when(deskRepository.findById(anyString())).thenReturn(Optional.of(mock(Desk.class)));

    service = new ReservationService(reservationRepository, roomRepository, deskRepository);
    doAnswer(invocation -> invocation.getArgument(0)).when(reservationRepository).save(any());

    info = mock(ReservationInfo.class);
    when(info.getDeskId()).thenReturn("desk");
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(10));
    when(info.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(30));

    fakeReservation1 = mock(Reservation.class);
    fakeReservation2 = mock(Reservation.class);
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().plusMinutes(40));
    when(fakeReservation1.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(50));
    when(fakeReservation2.getStart()).thenReturn(LocalDateTime.now().plusMinutes(60));
    when(fakeReservation2.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(80));
    Stream<Reservation> reservationStream1 = Stream.of(fakeReservation1, fakeReservation2);

    fakeReservation3 = mock(Reservation.class);
    Reservation fakeReservation4 = mock(Reservation.class);
    when(fakeReservation3.getStart()).thenReturn(LocalDateTime.now().minusMinutes(30));
    when(fakeReservation3.getEnd()).thenReturn(LocalDateTime.now());
    when(fakeReservation4.getStart()).thenReturn(LocalDateTime.now().minusHours(2));
    when(fakeReservation4.getEnd()).thenReturn(LocalDateTime.now().minusHours(1));
    Stream<Reservation> reservationStream2 = Stream.of(fakeReservation3, fakeReservation4);

    when(reservationRepository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenReturn(reservationStream1);
    when(reservationRepository.findReservationsByDeskIdAndEndIsBefore(any(), any()))
        .thenReturn(reservationStream2);
  }

  @Test
  void validAddition() throws ReservationClash, BadTimeIntervals {
    Reservation added = service.addReservation(info, username);
    assertEquals(info.getDeskId(), added.getDeskId());
    assertEquals(info.getStart(), added.getStart());
    assertEquals(info.getEnd(), added.getEnd());
    assertEquals(username, added.getUsername());
  }

  @Test
  void clashWithReservationEndingAfterStart() {
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().plusMinutes(20));
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }

  @Test
  void clashWithReservationStartingBeforeEnding() {
    when(fakeReservation3.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(15));
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }

  @Test
  void findIfTimeFallsInto_callsRepository() {
    AtomicBoolean repositoryCalled = new AtomicBoolean(false);
    when(reservationRepository.findReservationsByDeskIdAndStartIsBeforeAndEndIsAfter(
            any(), any(), any()))
        .thenAnswer(
            invocation -> {
              LocalDateTime tmp = invocation.getArgument(1);
              LocalDateTime tmp1 = invocation.getArgument(2);
              assertEquals(tmp, tmp1);
              repositoryCalled.set(true);
              return null;
            });
    LocalDateTime fakeTime = LocalDateTime.MIN;
    service.findIfTimeFallsInto("id", fakeTime);
    assertTrue(repositoryCalled.get());
  }

  @Test
  void nextReservationSortsAccordingToReservationComparable() {
    Reservation fake1 = mock(Reservation.class);
    Reservation fake2 = mock(Reservation.class);
    when(fake1.compareTo(fake2)).thenReturn(-1);
    when(fake2.compareTo(fake1)).thenReturn(1);
    when(reservationRepository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenAnswer(
            invocation -> {
              assertEquals("id", invocation.getArgument(0));
              assertEquals(LocalDateTime.MIN, invocation.getArgument(1));
              return Stream.of(fake2, fake1);
            });
    assertEquals(fake1, service.nextReservation("id", LocalDateTime.MIN).get());
  }

  @Test
  void findById() {
    Reservation expectedReservation = mock(Reservation.class);
    when(reservationRepository.findReservationById("idReservation"))
        .thenReturn(Optional.of(expectedReservation));
    assertEquals(expectedReservation, service.findById("idReservation"));
  }

  @Test
  void save_endConflict_throwsReservationClash() {
    Reservation providedReservation =
        new Reservation(
            "idDesk",
            "username",
            LocalDateTime.now().plusMinutes(60),
            LocalDateTime.now().plusMinutes(120));
    Reservation conflictReservation = mock(Reservation.class);
    when(conflictReservation.getStart()).thenReturn(LocalDateTime.now().plusMinutes(90));
    when(reservationRepository.findReservationsByDeskIdAndStartIsAfter(
            "idDesk", providedReservation.getStart()))
        .thenReturn(Stream.of(conflictReservation));
    assertThrows(ReservationClash.class, () -> service.save(providedReservation));
  }

  @Test
  void save_startConflict_throwsReservationClash() {
    Reservation providedReservation =
        new Reservation(
            "idDesk",
            "username",
            LocalDateTime.now().plusMinutes(60),
            LocalDateTime.now().plusMinutes(120));
    Reservation conflictReservation = mock(Reservation.class);
    when(conflictReservation.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(90));
    when(reservationRepository.findReservationsByDeskIdAndEndIsBefore(
            "idDesk", providedReservation.getEnd()))
        .thenReturn(Stream.of(conflictReservation));
    assertThrows(ReservationClash.class, () -> service.save(providedReservation));
  }

  @Test
  void delete_validId() {
    Reservation expectedReservation = mock(Reservation.class);
    when(reservationRepository.deleteReservationById("idReservation"))
        .thenReturn(Optional.of(expectedReservation));
    assertEquals(expectedReservation, service.delete("idReservation"));
  }

  @Test
  void delete_idNotFound_throwsNoSuchReservation() {
    when(reservationRepository.deleteReservationById(anyString())).thenReturn(Optional.empty());
    assertThrows(NoSuchReservation.class, () -> service.delete("idReservation"));
  }

  @Test
  void findByUsernameAndStart_futureReservationsPresentAndUserCurrentlyReserving() {
    ArrayList<Reservation> fakeList = mock(ArrayList.class);
    when(reservationRepository.findReservationsByUsernameAndStartIsAfter(anyString(), any()))
        .thenReturn(fakeList);
    when(reservationRepository.findReservationByUsernameAndStartIsBeforeAndEndIsAfter(
            anyString(), any(), any()))
        .thenReturn(Optional.of(mock(Reservation.class)));
    assertEquals(fakeList, service.findByUsernameAndStart("user", LocalDateTime.now()));
  }

  @Test
  void findByUsernameAndStart_futureReservationsPresentButUserNotCurrentlyReserving() {
    ArrayList<Reservation> fakeList = mock(ArrayList.class);
    when(reservationRepository.findReservationsByUsernameAndStartIsAfter(anyString(), any()))
        .thenReturn(fakeList);
    when(reservationRepository.findReservationByUsernameAndStartIsBeforeAndEndIsAfter(
            anyString(), any(), any()))
        .thenReturn(Optional.empty());
    assertEquals(fakeList, service.findByUsernameAndStart("user", LocalDateTime.now()));
  }

  @Test
  void findByUsernameAndStart_futureReservationsEmptyAndUserCurrentlyReserving() {
    Reservation fakeReservation = mock(Reservation.class);
    when(reservationRepository.findReservationsByUsernameAndStartIsAfter(anyString(), any()))
        .thenReturn(new ArrayList<>());
    when(reservationRepository.findReservationByUsernameAndStartIsBeforeAndEndIsAfter(
            anyString(), any(), any()))
        .thenReturn(Optional.of(fakeReservation));
    assertFalse(service.findByUsernameAndStart("user", LocalDateTime.now()).isEmpty());
  }

  @Test
  void findByUsernameAndStart_futureReservationsEmptyAndUserNotCurrentlyReserving() {
    when(reservationRepository.findReservationsByUsernameAndStartIsAfter(anyString(), any()))
        .thenReturn(new ArrayList<>());
    when(reservationRepository.findReservationByUsernameAndStartIsBeforeAndEndIsAfter(
            anyString(), any(), any()))
        .thenReturn(Optional.empty());
    assertEquals(
        Collections.emptyList(), service.findByUsernameAndStart("user", LocalDateTime.now()));
  }

  @Test
  void findByTimeInterval_startBeforeEnd() {
    LocalDateTime providedStart = LocalDateTime.now().plusMinutes(20),
        providedEnd = LocalDateTime.now().plusMinutes(40);
    Reservation mockReservation1 = mock(Reservation.class),
        mockReservation2 = mock(Reservation.class),
        mockReservation3 = mock(Reservation.class);
    when(mockReservation1.getStart()).thenReturn(providedStart);
    when(mockReservation2.getStart()).thenReturn(providedEnd);
    when(mockReservation3.getStart()).thenReturn(providedStart.plusMinutes(40));
    when(reservationRepository.findReservationByStartIsGreaterThanEqual(providedStart))
        .thenReturn(Stream.of(mockReservation1, mockReservation2, mockReservation3));
    assertEquals(
        List.of(mockReservation1, mockReservation2),
        service.findByTimeInterval(providedStart, providedEnd));
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
    when(fakeReservation1.getStart()).thenReturn(providedStart);
    when(fakeReservation2.getStart()).thenReturn(providedStart.plusMinutes(20));
    when(reservationRepository.findReservationByStartIsGreaterThanEqual(providedStart))
        .thenReturn(Stream.of(fakeReservation1, fakeReservation2));
    assertEquals(List.of(fakeReservation1), service.findByTimeInterval(providedStart, providedEnd));
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
}
