package it.sweven.blockcovid.routers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.EthereumRunner;
import it.sweven.blockcovid.entities.Reservation;
import it.sweven.blockcovid.entities.User;
import it.sweven.blockcovid.repositories.ReservationRepository;
import it.sweven.blockcovid.services.UserAuthenticationService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ReservationRouterTest {

  private ReservationRepository repository;
  private UserAuthenticationService service;
  private ReservationRouter router;
  private EthereumRunner ethereumRunner;

  @BeforeEach
  void setup() {
    repository = mock(ReservationRepository.class);
    service = mock(UserAuthenticationService.class);
    ethereumRunner = mock(EthereumRunner.class);
    router = new ReservationRouter(repository, service, ethereumRunner);
  }

  @Test
  void getSingleReservation_CorrectQuery_ExpectsReservation() {
    Reservation testReservation =
        new Reservation(
            "1234",
            "room1",
            5,
            LocalDate.of(2021, 10, 1),
            LocalTime.of(10, 30),
            LocalTime.of(11, 30),
            "user");
    when(repository.findByIdAndUser("1234", "user")).thenReturn(Optional.of(testReservation));
    User user = mock(User.class);
    when(user.getUsername()).thenReturn("user");
    when(service.authenticateByToken("authToken")).thenReturn(user);
    assertEquals(testReservation, router.getSingleReservation("1234", "authToken"));
  }

  @Test
  void getSingleReservation_WrongQuery_Expects404() {
    when(repository.findByIdAndUser("1234", "user")).thenReturn(Optional.empty());
    User user = mock(User.class);
    when(user.getUsername()).thenReturn("user");
    when(service.authenticateByToken("authToken")).thenReturn(user);
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class, () -> router.getSingleReservation("1234", "authToken"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }
}
