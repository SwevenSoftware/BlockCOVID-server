package it.sweven.blockcovid.routers.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.documents.CleanerPdfReport;
import it.sweven.blockcovid.entities.room.Room;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.RoomService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class AdminCleanerReportRouterTest {

  private RoomService service;
  private AdminCleanerReportRouter router;

  @BeforeEach
  void setUp() {
    service = mock(RoomService.class);
    when(service.getAllRooms()).thenReturn(Collections.emptyList());
    router = spy(new AdminCleanerReportRouter(service));
  }

  @Test
  void report_checkStreamIsRead() throws IOException {
    CleanerPdfReport mockReport = mock(CleanerPdfReport.class);
    when(router.getReport()).thenReturn(mockReport);
    doNothing().when(router).newReport();
    doNothing().when(router).loadFileInputStream(any());
    when(mockReport.save()).thenReturn("path");
    InputStream mockInput = mock(InputStream.class);
    when(router.getInputStream()).thenReturn(mockInput);
    AtomicBoolean streamRead = new AtomicBoolean(false);
    byte[] expectedBytes = "report returned".getBytes();
    when(mockInput.readAllBytes())
        .thenAnswer(
            invocation -> {
              streamRead.set(true);
              return expectedBytes;
            });
    assertEquals(expectedBytes, router.report(mock(User.class)));
    assertTrue(streamRead.get());
  }

  @Test
  void report_checkAllRoomAreRead() throws IOException {
    CleanerPdfReport mockReport = mock(CleanerPdfReport.class);
    when(router.getReport()).thenReturn(mockReport);
    doNothing().when(router).newReport();
    doNothing().when(router).loadFileInputStream(any());
    List<Room> expectedRooms = List.of(mock(Room.class), mock(Room.class));
    when(service.getAllRooms()).thenReturn(expectedRooms);
    when(mockReport.save()).thenReturn("path");
    InputStream mockInput = mock(InputStream.class);
    when(router.getInputStream()).thenReturn(mockInput);
    ArrayList<Room> providedRooms = new ArrayList<>();
    doAnswer(invocation -> providedRooms.add(invocation.getArgument(0)))
        .when(mockReport)
        .addRoom(any());
    router.report(null);
    assertEquals(expectedRooms, providedRooms);
  }

  @Test
  void report_errorWhileCreatingReport_throwsHttpStatus500() throws IOException {
    doThrow(new IOException()).when(router).newReport();
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileSavingReport_throwsHttpStatus500() throws IOException {
    CleanerPdfReport mockReport = mock(CleanerPdfReport.class);
    when(router.getReport()).thenReturn(mockReport);
    doNothing().when(router).newReport();
    when(mockReport.save()).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_errorWhileReadingSavedReport_throwsHttpStatus500() throws IOException {
    CleanerPdfReport mockReport = mock(CleanerPdfReport.class);
    when(router.getReport()).thenReturn(mockReport);
    doNothing().when(router).newReport();
    doThrow(new FileNotFoundException()).when(router).loadFileInputStream(any());
    when(mockReport.save()).thenReturn("path");
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> router.report(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
