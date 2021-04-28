package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.core.read.ListAppender;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.LoggingEvent;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class BlockchainCleanerControllerTest {
  private BlockchainService blockchainService;
  private DocumentService documentService;
  private BlockchainCleanerController controller;
  ListAppender<LoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    blockchainService = mock(BlockchainService.class);
    documentService = mock(DocumentService.class);
    RoomService roomService = mock(RoomService.class);
    DocumentContractService documentContractService = mock(DocumentContractService.class);
    Credentials accountCredentials = mock(Credentials.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    controller =
        new BlockchainCleanerController(
            blockchainService,
            documentContractService,
            documentService,
            roomService,
            accountCredentials);
    Logger logger = LoggerFactory.getLogger(BlockchainCleanerController.class);
    LogManager.getLogger("BlockchainCleanerController");
  }

  @Test
  void runWithNoProblemsShouldNotThrowException() throws Exception {
    TransactionReceipt fakeReceipt = mock(TransactionReceipt.class);
    when(documentService.generateCleanerReport(any())).thenReturn("path");
    when(blockchainService.registerReport(any(), any())).thenReturn(fakeReceipt);
    Files.createFile(Path.of("path"));
    assertDoesNotThrow(controller::run);
    Files.delete(Path.of("path"));
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void invalidSavedPath_throwsIoException() throws IOException {
    when(documentService.generateCleanerReport(any())).thenReturn("InvalidPath");
    assertThrows(IOException.class, controller::run);
  }

  @Test
  void registerReportFails_throwsException() throws Exception {
    when(documentService.generateCleanerReport(any())).thenReturn("path");
    when(blockchainService.registerReport(any(), any())).thenThrow(new Exception());
    Files.createFile(Path.of("path"));
    assertThrows(Exception.class, controller::run);
    Files.delete(Path.of("path"));
  }
}
