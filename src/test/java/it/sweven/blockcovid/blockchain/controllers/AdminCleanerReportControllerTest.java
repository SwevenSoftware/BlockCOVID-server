package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminCleanerReportControllerTest {

  private RoomService roomService;
  private DocumentService documentService;
  private AdminCleanerReportController controller;
  private BlockchainService blockchainService;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    documentService = mock(DocumentService.class);
    blockchainService = mock(BlockchainService.class);
    BlockchainDeploymentInformation credentials = mock(BlockchainDeploymentInformation.class);
    when(roomService.getAllRooms()).thenReturn(Collections.emptyList());
    controller =
        spy(
            new AdminCleanerReportController(
                roomService, documentService, blockchainService, credentials));
  }

  @Test
  void report_successfulReturnNewReport() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    byte[] expectedBytes = "correct result".getBytes();
    when(documentService.readReport("reportPath")).thenReturn(expectedBytes);
    Files.createFile(Path.of("reportPath"));
    assertEquals(expectedBytes, controller.report(mock(User.class)));
    Files.deleteIfExists(Path.of("reportPath"));
  }

  @Test
  void report_errorWhileCreatingReport() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileReadingReport() throws IOException {
    when(documentService.readReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorLoadingContract() throws Exception {
    when(blockchainService.loadContract(any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
