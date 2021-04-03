package it.sweven.blockcovid.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.core.read.ListAppender;
import it.sweven.blockcovid.controllers.blockchain.BlockchainController;
import it.sweven.blockcovid.services.BlockchainService;
import it.sweven.blockcovid.services.DocumentContractService;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
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

class BlockchainControllerTest {
  private BlockchainService blockchainService;
  private DocumentContractService documentContractService;
  private Credentials accountCredentials;
  private DocumentService documentService;
  private RoomService roomService;
  private BlockchainController controller;
  private Logger logger;
  ListAppender<LoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    blockchainService = mock(BlockchainService.class);
    documentService = mock(DocumentService.class);
    roomService = mock(RoomService.class);
    documentContractService = mock(DocumentContractService.class);
    accountCredentials = mock(Credentials.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    controller =
        new BlockchainController(
            blockchainService,
            documentContractService,
            documentService,
            roomService,
            accountCredentials);
    logger = LoggerFactory.getLogger(BlockchainController.class);
    LogManager.getLogger("BlockchainController");
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
