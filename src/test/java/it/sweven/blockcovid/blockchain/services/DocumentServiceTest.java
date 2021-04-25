package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.documents.PdfReport;
import it.sweven.blockcovid.reservations.entities.Reservation;
import it.sweven.blockcovid.reservations.entities.ReservationBuilder;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentServiceTest {

  private DocumentService service;

  @BeforeEach
  void setUp() {
    service = spy(new DocumentService());
  }

  @Test
  void generateCleanerReport_reportCorrectlyCreated()
      throws IOException, BadAttributeValueExpException {
    doReturn("pathFile").when(service).initializeReport(any());
    Room mockRoom1 = mock(Room.class), mockRoom2 = mock(Room.class);
    when(mockRoom1.getName()).thenReturn("room1");
    when(mockRoom1.getRoomStatus()).thenReturn(Status.CLEAN);
    when(mockRoom2.getName()).thenReturn("room2");
    when(mockRoom2.getRoomStatus()).thenReturn(Status.DIRTY);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    assertEquals("pathFile", service.generateCleanerReport(List.of(mockRoom1, mockRoom2)));
    verify(mockReport).create("pathFile");
  }

  @Test
  void generateCleanerReport_errorWhileCreatingReport_throwsIOException()
      throws IOException, BadAttributeValueExpException {
    doReturn("pathFile").when(service).initializeReport(any());
    Room mockRoom1 = mock(Room.class), mockRoom2 = mock(Room.class);
    when(mockRoom1.getName()).thenReturn("room1");
    when(mockRoom1.getRoomStatus()).thenReturn(Status.CLEAN);
    when(mockRoom2.getName()).thenReturn("room2");
    when(mockRoom2.getRoomStatus()).thenReturn(Status.DIRTY);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    doThrow(new BadAttributeValueExpException(null)).when(mockReport).create(any());
    assertThrows(
        IOException.class, () -> service.generateCleanerReport(List.of(mockRoom1, mockRoom2)));
  }

  @Test
  void generateUsageReport_reportCorrectlyCreated()
      throws BadAttributeValueExpException, IOException {
    doReturn("pathFile").when(service).initializeReport(any());
    Reservation
        reservation1 =
            new ReservationBuilder()
                .id("id1")
                .deskId("deskId1")
                .username("username")
                .start(LocalDateTime.MIN.withHour(10))
                .end(LocalDateTime.MIN.withHour(13))
                .realStart(LocalDateTime.MIN.withHour(11))
                .realEnd(LocalDateTime.MIN.withHour(12))
                .deskCleaned(true)
                .build(),
        reservation2 =
            new ReservationBuilder()
                .id("id2")
                .deskId("deskId2")
                .username("username")
                .start(LocalDateTime.MIN.withHour(15))
                .end(LocalDateTime.MIN.withHour(18))
                .realStart(LocalDateTime.MIN.withHour(14))
                .realEnd(LocalDateTime.MIN.withHour(17))
                .deskCleaned(false)
                .build();
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    assertEquals("pathFile", service.generateUsageReport(List.of(reservation1, reservation2)));
    verify(mockReport).create("pathFile");
  }

  @Test
  void generateUsageReport_errorWhileCreatingReport_throwsIOException()
      throws IOException, BadAttributeValueExpException {
    doReturn("pathFile").when(service).initializeReport(any());
    Reservation
        reservation1 =
            new ReservationBuilder()
                .id("id1")
                .deskId("deskId1")
                .username("username")
                .start(LocalDateTime.MIN.withHour(10))
                .end(LocalDateTime.MIN.withHour(13))
                .realStart(LocalDateTime.MIN.withHour(11))
                .realEnd(LocalDateTime.MIN.withHour(12))
                .deskCleaned(true)
                .build(),
        reservation2 =
            new ReservationBuilder()
                .id("id2")
                .deskId("deskId2")
                .username("username")
                .start(LocalDateTime.MIN.withHour(15))
                .end(LocalDateTime.MIN.withHour(18))
                .realStart(LocalDateTime.MIN.withHour(14))
                .realEnd(LocalDateTime.MIN.withHour(17))
                .deskCleaned(false)
                .build();
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    doThrow(new BadAttributeValueExpException(null)).when(mockReport).create(any());
    assertThrows(
        IOException.class, () -> service.generateUsageReport(List.of(reservation1, reservation2)));
  }

  @Test
  void initializeReport() throws IOException {
    doReturn("pathFile").when(service).pathReport(any());
    doReturn(false).when(service).fileExists(any());
    doNothing().when(service).createDirectory(any());
    doNothing().when(service).createFile(any());
    assertEquals("pathFile", service.initializeReport(LocalDateTime.of(2021, 1, 1, 20, 0)));
  }

  @Test
  void pathReport() {
    assertTrue(service.pathReport("idPath").contains("idPath"));
  }
}
