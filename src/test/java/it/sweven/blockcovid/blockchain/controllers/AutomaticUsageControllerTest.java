package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.core.read.ListAppender;
import it.sweven.blockcovid.blockchain.services.DeploymentService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.event.LoggingEvent;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class AutomaticUsageControllerTest {
  private DeploymentService deploymentService;
  private ReservationService reservationService;
  private DocumentService documentService;
  private AutomaticUsageController controller;
  ListAppender<LoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    deploymentService = mock(DeploymentService.class);
    documentService = mock(DocumentService.class);
    reservationService = mock(ReservationService.class);
    DocumentContract contract = mock(DocumentContract.class);
    controller =
        new AutomaticUsageController(
            deploymentService, documentService, reservationService, contract);
  }

  @Test
  void runWithNoProblemsShouldNotThrowException() throws Exception {
    TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
    List<ReservationWithRoom> listReservations =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(listReservations);
    when(documentService.generateUsageReport(listReservations)).thenReturn("path");
    when(deploymentService.registerReport(any(), any())).thenReturn(fakeReceipt);
    Files.createFile(Path.of("path"));
    assertDoesNotThrow(controller::run);
    Files.delete(Path.of("path"));
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(documentService.generateUsageReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void invalidSavedPath_throwsIoException() throws IOException {
    when(documentService.generateUsageReport(any())).thenReturn("InvalidPath");
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void registerReportFails_throwsException() throws Exception {
    when(documentService.generateUsageReport(any())).thenReturn("path");
    when(deploymentService.registerReport(any(), any())).thenThrow(new Exception());
    Files.createFile(Path.of("path"));
    assertThrows(Exception.class, controller::run);
    Files.delete(Path.of("path"));
  }
}
