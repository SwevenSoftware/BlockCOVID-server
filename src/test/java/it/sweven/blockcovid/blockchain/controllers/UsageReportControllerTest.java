package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;

class UsageReportControllerTest {

  private ReservationService reservationService;
  private DocumentService documentService;
  private UsageReportController controller;
  private BlockchainService blockchainService;
  private DocumentContractService documentContractService;

  private final DocumentContract documentContract = mock(DocumentContract.class);

  @BeforeEach
  void setUp() throws Exception {
    reservationService = mock(ReservationService.class);
    documentService = mock(DocumentService.class);
    blockchainService = mock(BlockchainService.class);
    documentContractService = mock(DocumentContractService.class);
    Credentials credentials = mock(Credentials.class);
    when(documentContractService.getContractByAccount(credentials)).thenReturn(documentContract);
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(Collections.emptyList());
    controller =
        new UsageReportController(
            reservationService,
            documentService,
            blockchainService,
            documentContractService,
            credentials);
  }

  @Test
  void report_reportCorrectlyGenerated() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    List<ReservationWithRoom> listReservations =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    when(reservationService.findByTimeInterval(from, to)).thenReturn(listReservations);
    when(documentService.generateUsageReport(listReservations)).thenReturn("pathReport");
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "correct report".getBytes();
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
