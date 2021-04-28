package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class AutomaticUsageControllerTest {
  private SignRegistrationService signRegistrationService;
  private ReservationService reservationService;
  private DocumentService documentService;
  private AutomaticUsageController controller;

  @BeforeEach
  void setUp() {
    documentService = mock(DocumentService.class);
    reservationService = mock(ReservationService.class);
    signRegistrationService = mock(SignRegistrationService.class);
    controller =
        new AutomaticUsageController(documentService, signRegistrationService, reservationService);
  }

  @Test
  void runWithNoProblemsShouldNotThrowException() throws Exception {
    List<ReservationWithRoom> listReservations =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(listReservations);
    when(documentService.generateUsageReport(listReservations)).thenReturn("path");
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ONE);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    assertDoesNotThrow(controller::run);
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(documentService.generateUsageReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void invalidSavedPath_doesNotThrow() throws Exception {
    when(documentService.generateUsageReport(any())).thenReturn("InvalidPath");
    when(documentService.hashOf(any())).thenThrow(new IOException());
    assertDoesNotThrow(controller::run);
  }

  @Test
  void registerReportFails_doesNotThrow() throws Exception {
    when(documentService.generateUsageReport(any())).thenReturn("path");
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ONE);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    assertDoesNotThrow(controller::run);
  }
}
