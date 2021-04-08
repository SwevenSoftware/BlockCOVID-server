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
    when(info.getStart()).thenReturn(LocalDateTime.MIN);
    when(info.getEnd()).thenReturn(LocalDateTime.MAX);
    String username = "username";
    Stream<Reservation> fakeStream = mock(Stream.class);
    when(repository.findReservationsByDeskIdAndEndIsBefore(any(), any())).thenReturn(fakeStream);
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any())).thenReturn(fakeStream);
    when(fakeStream.parallel()).thenReturn(fakeStream);
    when(fakeStream.anyMatch(any())).thenReturn(false);
    Reservation added = service.addReservation(info, username);
    assertEquals(LocalDateTime.MIN, added.getStart());
    assertEquals(LocalDateTime.MAX, added.getEnd());
    assertEquals("desk", added.getDeskId());
  }

  @Test
  void Clash() throws ReservationClash {
    ReservationInfo info = mock(ReservationInfo.class);
    String username = "username";
    Stream<Reservation> fakeStream = mock(Stream.class);
    when(repository.findReservationsByDeskIdAndEndIsBefore(any(), any())).thenReturn(fakeStream);
    when(repository.findReservationsByDeskIdAndStartIsAfter(any(), any())).thenReturn(fakeStream);
    when(fakeStream.parallel()).thenReturn(fakeStream);
    when(fakeStream.anyMatch(any())).thenReturn(true);
    assertThrows(ReservationClash.class, () -> service.addReservation(info, username));
  }
}
