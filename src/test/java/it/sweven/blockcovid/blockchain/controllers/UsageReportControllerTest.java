package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.entities.ReservationBuilder;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;

class UsageReportControllerTest {

  private ReservationService reservationService;
  private ReservationBuilder reservationBuilder;
  private DocumentService documentService;
  private UsageReportController controller;
  private BlockchainService blockchainService;
  private DocumentContractService documentContractService;

  private final DocumentContract documentContract = mock(DocumentContract.class);

  @BeforeEach
  void setUp() throws Exception {
    reservationService = mock(ReservationService.class);
    reservationBuilder = mock(ReservationBuilder.class);
    when(reservationBuilder.from(any())).thenReturn(reservationBuilder);
    documentService = mock(DocumentService.class);
    blockchainService = mock(BlockchainService.class);
    documentContractService = mock(DocumentContractService.class);
    Credentials credentials = mock(Credentials.class);
    when(documentContractService.getContractByAccount(credentials)).thenReturn(documentContract);
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(Collections.emptyList());
    controller =
        new UsageReportController(
            reservationService,
            reservationBuilder,
            documentService,
            blockchainService,
            documentContractService,
            credentials);
  }

  @Test
  void report_reportCorrectlyGenerated() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    Reservation reservation = mock(Reservation.class);
    when(reservation.getRealStart()).thenReturn(LocalDateTime.MIN.withHour(14));
    when(reservation.getRealEnd()).thenReturn(LocalDateTime.MIN.withHour(17));
    when(reservationBuilder.build()).thenReturn(reservation);
    when(reservationService.findByTimeInterval(from, to))
        .thenReturn(List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class)));
    when(documentService.generateUsageReport(List.of(reservation, reservation)))
        .thenReturn("pathReport");
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "correct report".getBytes();
    when(documentService.readReport("pathReport")).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class), from, to));
    verify(blockchainService).registerReport(eq(documentContract), any());
    Files.deleteIfExists(Path.of("pathReport"));
  }

  @Test
  void report_reservationsNotBuilt_returnEmptyReport() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    when(reservationBuilder.build()).thenThrow(new BadAttributeValueExpException(""));
    when(reservationService.findByTimeInterval(from, to))
        .thenReturn(List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class)));
    when(documentService.generateUsageReport(Collections.emptyList())).thenReturn("pathReport");
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "empty report".getBytes();
    when(documentService.readReport("pathReport")).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class), from, to));
    verify(blockchainService).registerReport(eq(documentContract), any());
    Files.deleteIfExists(Path.of("pathReport"));
  }

  @Test
  void report_reservationsNotStarted_returnEmptyReport() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    Reservation reservation = mock(Reservation.class);
    when(reservationBuilder.build()).thenReturn(reservation);
    when(reservationService.findByTimeInterval(from, to))
        .thenReturn(List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class)));
    when(documentService.generateUsageReport(Collections.emptyList())).thenReturn("pathReport");
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "empty report".getBytes();
    when(documentService.readReport("pathReport")).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class), from, to));
    verify(blockchainService).registerReport(eq(documentContract), any());
    Files.deleteIfExists(Path.of("pathReport"));
  }

  @Test
  void report_reservationsNotEnded_returnEmptyReport() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    Reservation reservation = mock(Reservation.class);
    when(reservation.getRealStart()).thenReturn(LocalDateTime.MIN.withHour(14));
    when(reservationBuilder.build()).thenReturn(reservation);
    when(reservationService.findByTimeInterval(from, to))
        .thenReturn(List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class)));
    when(documentService.generateUsageReport(Collections.emptyList())).thenReturn("pathReport");
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "empty report".getBytes();
    when(documentService.readReport("pathReport")).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class), from, to));
    verify(blockchainService).registerReport(eq(documentContract), any());
    Files.deleteIfExists(Path.of("pathReport"));
  }

  @Test
  void report_reportGenerationFails_throwsResponseStatusException() throws IOException {
    when(documentService.generateUsageReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_contractCreationFails_throwsResponseStatusException() throws Exception {
    when(documentService.generateUsageReport(any())).thenReturn("pathReport");
    when(documentContractService.getContractByAccount(any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_reportRegistrationFails_throwsResponseStatusException() throws Exception {
    when(documentService.generateUsageReport(any())).thenReturn("pathReport");
    when(blockchainService.registerReport(any(), any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_reportReadFails_throwsResponseStatusException() throws IOException {
    when(documentService.generateUsageReport(any())).thenReturn("pathReport");
    when(documentService.readReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
