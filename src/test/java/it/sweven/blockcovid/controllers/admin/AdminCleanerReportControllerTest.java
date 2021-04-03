package it.sweven.blockcovid.controllers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.BlockchainService;
import it.sweven.blockcovid.services.DocumentContractService;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;

class AdminCleanerReportControllerTest {

  private RoomService roomService;
  private DocumentService documentService;
  private AdminCleanerReportController router;
  private BlockchainService blockchainService;
  private DocumentContractService documentContractService;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    documentService = mock(DocumentService.class);
    blockchainService = mock(BlockchainService.class);
    documentContractService = mock(DocumentContractService.class);
    Credentials credentials = mock(Credentials.class);
    when(roomService.getAllRooms()).thenReturn(Collections.emptyList());
    router =
        spy(
            new AdminCleanerReportController(
                roomService,
                documentService,
                blockchainService,
                documentContractService,
                credentials));
  }

  @Test
  void report_successfulReturnNewReport() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    byte[] expectedBytes = "correct result".getBytes();
    when(documentService.readReport("reportPath")).thenReturn(expectedBytes);
    Files.createFile(Path.of("reportPath"));
    assertEquals(expectedBytes, router.report(mock(User.class)));
    Files.deleteIfExists(Path.of("reportPath"));
  }

  @Test
  void report_errorWhileCreatingReport() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileReadingReport() throws IOException {
    when(documentService.readReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileRegisteringReport() throws Exception {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    when(blockchainService.registerReport(any(), any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileRetrievingContract() throws Exception {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    when(documentContractService.getContractByAccount(any())).thenThrow(new Exception());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
