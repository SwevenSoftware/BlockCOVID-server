package it.sweven.blockcovid.reservations.servicies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservationServiceTest {
  private ReservationRepository repository;
  private ReservationService service;

  @BeforeEach
  void setUp() {
    repository = mock(ReservationRepository.class);
    service = new ReservationService(repository);
    doAnswer(invocation -> invocation.getArgument(0)).when(repository).save(any());
  }

  @Test
  void validAddition() throws ReservationClash {
    ReservationInfo info = mock(ReservationInfo.class);
    when(info.getDeskId()).thenReturn("desk");
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(60));
    when(info.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(120));
    String username = "username";
    Reservation fakeReservation1 = mock(Reservation.class);
    Reservation fakeReservation2 = mock(Reservation.class);
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().plusMinutes(180));
    when(fakeReservation1.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(270));
    when(fakeReservation2.getStart()).thenReturn(LocalDateTime.now().plusMinutes(360));
    when(fakeReservation2.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(920));
    Stream<Reservation> reservationStream1 = Stream.of(fakeReservation1, fakeReservation2);
    Reservation fakeReservation3 = mock(Reservation.class);
    Reservation fakeReservation4 = mock(Reservation.class);
    when(fakeReservation3.getStart()).thenReturn(LocalDateTime.now().minusMinutes(30));
    when(fakeReservation3.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(30));
    when(fakeReservation4.getStart()).thenReturn(LocalDateTime.now().minusMinutes(180));
    when(fakeReservation4.getEnd()).thenReturn(LocalDateTime.now().minusMinutes(60));
    Stream<Reservation> reservationStream2 = Stream.of(fakeReservation3, fakeReservation4);
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenReturn(reservationStream1);
    when(repository.findReservationsByDeskIdAndEndIsBefore(any(), any()))
        .thenReturn(reservationStream2);
    Reservation added = service.addReservation(info, username);
    assertEquals(info.getDeskId(), added.getDeskId());
    assertEquals(info.getStart(), added.getStart());
    assertEquals(info.getEnd(), added.getEnd());
    assertEquals(username, added.getUsername());
  }

  @Test
  void clashWithReservationEndingAfterStart() {
    ReservationInfo info = mock(ReservationInfo.class);
    when(info.getDeskId()).thenReturn("desk");
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(60));
    when(info.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(120));
    String username = "username";
    Reservation fakeReservation1 = mock(Reservation.class);
    Reservation fakeReservation2 = mock(Reservation.class);
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().plusMinutes(80));
    when(fakeReservation1.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(120));
    when(fakeReservation2.getStart()).thenReturn(LocalDateTime.now().plusMinutes(130));
    when(fakeReservation2.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(150));
    Stream<Reservation> reservationStream1 = Stream.of(fakeReservation1, fakeReservation2);
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenReturn(reservationStream1);
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }

  @Test
  void clashWithReservationStartingBeforeEnding() {
    ReservationInfo info = mock(ReservationInfo.class);
    when(info.getDeskId()).thenReturn("desk");
    when(info.getStart()).thenReturn(LocalDateTime.now().plusMinutes(60));
    when(info.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(120));
    String username = "username";
    Reservation fakeReservation1 = mock(Reservation.class);
    Reservation fakeReservation2 = mock(Reservation.class);
    when(fakeReservation1.getStart()).thenReturn(LocalDateTime.now().plusMinutes(180));
    when(fakeReservation1.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(270));
    when(fakeReservation2.getStart()).thenReturn(LocalDateTime.now().plusMinutes(360));
    when(fakeReservation2.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(920));
    Stream<Reservation> reservationStream1 = Stream.of(fakeReservation1, fakeReservation2);
    Reservation fakeReservation3 = mock(Reservation.class);
    Reservation fakeReservation4 = mock(Reservation.class);
    when(fakeReservation3.getStart()).thenReturn(LocalDateTime.now().minusMinutes(30));
    when(fakeReservation3.getEnd()).thenReturn(LocalDateTime.now().plusMinutes(80));
    when(fakeReservation4.getStart()).thenReturn(LocalDateTime.now().minusMinutes(180));
    when(fakeReservation4.getEnd()).thenReturn(LocalDateTime.now().minusMinutes(60));
    Stream<Reservation> reservationStream2 = Stream.of(fakeReservation3, fakeReservation4);
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
        .thenReturn(reservationStream1);
    when(repository.findReservationsByDeskIdAndEndIsBefore(any(), any()))
        .thenReturn(reservationStream2);
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }

  @Test
  void findIfTimeFallsInto_callsRepository() {
    AtomicBoolean repositoryCalled = new AtomicBoolean(false);
    when(repository.findReservationsByDeskIdAndStartIsBeforeAndEndIsAfter(any(), any(), any()))
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
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any()))
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
    Optional<Reservation> expectedReservation = Optional.of(mock(Reservation.class));
    when(repository.findReservationById("idReservation")).thenReturn(expectedReservation);
    assertEquals(expectedReservation, service.findById("idReservation"));
  }

  @Test
  void save_validUpdate() throws ReservationClash {
    Reservation expectedReservation = mock(Reservation.class);
    when(repository.save(expectedReservation)).thenReturn(expectedReservation);
    assertEquals(expectedReservation, service.save(expectedReservation));
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
    when(repository.findReservationsByDeskIdAndStartIsAfter(
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
    when(repository.findReservationsByDeskIdAndEndIsBefore("idDesk", providedReservation.getEnd()))
        .thenReturn(Stream.of(conflictReservation));
    assertThrows(ReservationClash.class, () -> service.save(providedReservation));
  }
}
