package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.core.read.ListAppender;
import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.LoggingEvent;

class BlockchainControllerTest {
  private BlockchainService blockchainService;
  private DocumentService documentService;
  private BlockchainController controller;
  ListAppender<LoggingEvent> listAppender;

  @BeforeEach
  void setUp() {
    blockchainService = mock(BlockchainService.class);
    documentService = mock(DocumentService.class);
    RoomService roomService = mock(RoomService.class);
    DocumentContractService documentContractService = mock(DocumentContractService.class);
    BlockchainDeploymentInformation deploymentInformation =
        mock(BlockchainDeploymentInformation.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    controller =
        new BlockchainController(
            blockchainService,
            documentContractService,
            documentService,
            roomService,
            deploymentInformation);
    Logger logger = LoggerFactory.getLogger(BlockchainController.class);
    LogManager.getLogger("BlockchainController");
  }

  @Test
  void generationOfCleanerReportThrowsException_throwsIoException() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    assertThrows(IOException.class, controller::run);
  }
}
