package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.services.ReportService;
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
  private ReportService reportService;
  private AutomaticUsageController controller;

  @BeforeEach
  void setUp() {
    reportService = mock(ReportService.class);
    reservationService = mock(ReservationService.class);
    signRegistrationService = mock(SignRegistrationService.class);
    controller =
        new AutomaticUsageController(reportService, signRegistrationService, reservationService);
  }

  @Test
  void runWithNoProblemsShouldNotThrowException() throws Exception {
    List<ReservationWithRoom> listReservations =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(listReservations);
    when(reportService.generateUsageReport(listReservations))
        .thenReturn(mock(ReportInformation.class));
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ONE);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    assertDoesNotThrow(controller::run);
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(reportService.generateUsageReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void invalidSavedPath_doesNotThrow() throws Exception {
    when(reportService.generateUsageReport(any())).thenReturn(mock(ReportInformation.class));
    when(reportService.hashOf(any())).thenThrow(new IOException());
    assertDoesNotThrow(controller::run);
  }

  @Test
  void registerReportFails_doesNotThrow() throws Exception {
    when(reportService.generateUsageReport(any())).thenReturn(mock(ReportInformation.class));
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ONE);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    assertDoesNotThrow(controller::run);
  }
}
