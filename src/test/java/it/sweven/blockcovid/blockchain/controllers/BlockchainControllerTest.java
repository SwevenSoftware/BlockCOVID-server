package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.services.DeploymentInformationService;
import it.sweven.blockcovid.blockchain.services.DeploymentService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

class BlockchainControllerTest {
  private DeploymentService deploymentService;
  private DocumentService documentService;
  private BlockchainController controller;

  @BeforeEach
  void setUp() throws IOException {
    deploymentService = mock(DeploymentService.class);
    documentService = mock(DocumentService.class);
    when(documentService.generateCleanerReport(any())).thenReturn("");
    RoomService roomService = mock(RoomService.class);
    DeploymentInformationService deploymentInformationService =
        mock(DeploymentInformationService.class);
    BlockchainDeploymentInformation deploymentInformation =
        mock(BlockchainDeploymentInformation.class);
    controller =
        new BlockchainController(
            deploymentService,
            deploymentInformationService,
            documentService,
            roomService,
            deploymentInformation);
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
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(documentService.generateCleanerReport(any())).thenReturn(path);
    when(deploymentService.registerReport(any(), any())).thenReturn(receipt);
    Files.createFile(Path.of(path));
    when(documentService.setAsVerified(any())).thenReturn(path);
    assertDoesNotThrow(() -> controller.run());
    Files.deleteIfExists(Path.of(path));
  }

  @Test
  void unableToReadFile() throws Exception {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(documentService.generateCleanerReport(any())).thenReturn(path);
    when(deploymentService.registerReport(any(), any())).thenReturn(receipt);
    when(documentService.setAsVerified(any())).thenReturn(path);
    assertDoesNotThrow(() -> controller.run());
  }

  @Test
  void exceptionThrown() throws Exception {
    String path = "Report.pdf";
    TransactionReceipt receipt = mock(TransactionReceipt.class);
    when(receipt.getBlockNumber()).thenReturn(BigInteger.ZERO);
    when(documentService.generateCleanerReport(any())).thenReturn(path);
    when(deploymentService.registerReport(any(), any())).thenReturn(receipt);
    when(documentService.setAsVerified(any())).thenReturn(path);
    when(deploymentService.loadContract(any())).thenThrow(new Exception());
    assertDoesNotThrow(() -> controller.run());
  }
}
