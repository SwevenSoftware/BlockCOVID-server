package it.sweven.blockcovid.reservations.servicies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.reservations.dto.ReservationInfo;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.exceptions.ReservationClash;
import it.sweven.blockcovid.reservations.repositories.ReservationRepository;
import java.time.LocalDateTime;
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
}
