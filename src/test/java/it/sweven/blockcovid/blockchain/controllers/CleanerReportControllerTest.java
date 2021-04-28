package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CleanerReportControllerTest {

  private RoomService roomService;
  private DocumentService documentService;
  private CleanerReportController controller;

  @BeforeEach
  void setUp() {
    roomService = mock(RoomService.class);
    documentService = mock(DocumentService.class);
    SignRegistrationService signRegistrationService = mock(SignRegistrationService.class);
    when(roomService.getAllRooms()).thenReturn(Collections.emptyList());
    controller = new CleanerReportController(roomService, documentService, signRegistrationService);
  }

  @Test
  void report_successfulReturnNewReport() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    byte[] expectedBytes = "correct result".getBytes();
    when(documentService.readReport("reportPath")).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class)));
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
  void report_setAsVerifiedThrows_doesNotThrow() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(documentService.generateCleanerReport(mockRooms)).thenReturn("reportPath");
    byte[] expectedBytes = "correct result".getBytes();
    when(documentService.readReport("reportPath")).thenReturn(expectedBytes);
    when(documentService.setAsVerified(any())).thenThrow(new IOException());
    assertEquals(expectedBytes, controller.report(mock(User.class)));
  }
}
