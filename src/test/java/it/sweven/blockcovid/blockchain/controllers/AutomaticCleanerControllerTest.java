package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.exceptions.ReportNotFoundException;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class AutomaticCleanerControllerTest {
  SignRegistrationService signRegistrationService;
  ReportService reportService;
  RoomService roomService;

  AutomaticCleanerController controller;

  @BeforeEach
  void setUp() {
    signRegistrationService = mock(SignRegistrationService.class);
    reportService = mock(ReportService.class);
    roomService = mock(RoomService.class);
    controller =
        new AutomaticCleanerController(signRegistrationService, reportService, roomService);
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(reportService.generateCleanerReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void happyPath() throws Exception, ReportNotFoundException {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(reportService.generateCleanerReport(any())).thenReturn(mock(ReportInformation.class));
    when(reportService.setAsVerified(any(), any())).thenReturn(mock(ReportInformation.class));
    assertDoesNotThrow(() -> controller.run());
  }

  @Test
  void unableToReadFile() throws Exception, ReportNotFoundException {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(reportService.generateCleanerReport(any())).thenReturn(mock(ReportInformation.class));
    when(reportService.setAsVerified(any(), any())).thenThrow(new IOException());
    assertDoesNotThrow(() -> controller.run());
  }
}
