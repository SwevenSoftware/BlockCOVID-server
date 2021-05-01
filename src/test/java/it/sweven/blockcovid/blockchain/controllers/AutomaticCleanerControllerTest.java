package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class AutomaticCleanerControllerTest {
  SignRegistrationService signRegistrationService;
  DocumentService documentService;
  RoomService roomService;

  AutomaticCleanerController controller;

  @BeforeEach
  void setUp() {
    signRegistrationService = mock(SignRegistrationService.class);
    documentService = mock(DocumentService.class);
    roomService = mock(RoomService.class);
    controller =
        new AutomaticCleanerController(signRegistrationService, documentService, roomService);
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void happyPath() throws Exception {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(documentService.generateCleanerReport(any())).thenReturn(path);
    when(documentService.setAsVerified(any())).thenReturn(path);
    assertDoesNotThrow(() -> controller.run());
  }

  @Test
  void unableToReadFile() throws Exception {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(signRegistrationService.registerString(any())).thenReturn(receipt);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(documentService.generateCleanerReport(any())).thenReturn(path);
    when(documentService.setAsVerified(any())).thenThrow(new IOException());
    assertDoesNotThrow(() -> controller.run());
  }
}
