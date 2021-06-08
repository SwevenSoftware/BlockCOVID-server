package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.services.ReportService;
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
  private ReportService reportService;
  private CleanerReportController controller;
  private ReportInformation information;

  @BeforeEach
  void setUp() throws IOException {
    roomService = mock(RoomService.class);
    reportService = mock(ReportService.class);
    information = mock(ReportInformation.class);
    when(information.getPath()).thenReturn("path");
    when(reportService.generateCleanerReport(any())).thenReturn(information);
    SignRegistrationService signRegistrationService = mock(SignRegistrationService.class);
    when(roomService.getAllRooms()).thenReturn(Collections.emptyList());
    controller = new CleanerReportController(roomService, reportService, signRegistrationService);
  }

  @Test
  void report_successfulReturnNewReport() throws IOException {
    List<Room> mockRooms = List.of(mock(Room.class), mock(Room.class));
    when(roomService.getAllRooms()).thenReturn(mockRooms);
    when(reportService.generateCleanerReport(mockRooms)).thenReturn(information);
    byte[] expectedBytes = "correct result".getBytes();
    when(reportService.readReport(any())).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class)));
  }

  @Test
  void report_errorWhileCreatingReport() throws IOException {
    when(reportService.generateCleanerReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileReadingReport() throws IOException {
    when(reportService.readReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
