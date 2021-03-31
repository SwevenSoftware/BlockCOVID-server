package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminCleanerReportRouterTest {

  private RoomService roomService;
  private DocumentService documentService;
  private AdminCleanerReportRouter router;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    documentService = mock(DocumentService.class);
    when(roomService.getAllRooms()).thenReturn(Collections.emptyList());
    router = spy(new AdminCleanerReportRouter(roomService, documentService));
  }

  @Test
  void report_successfulReturnNewReport() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    byte[] expectedBytes = "correct result".getBytes();
    when(documentService.generateCleanerReport(mockRooms)).thenReturn(expectedBytes);
    assertEquals(expectedBytes, router.report(mock(User.class)));
  }

  @Test
  void report_errorWhileCreatingReport() throws IOException {
    when(documentService.generateCleanerReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
