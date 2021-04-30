package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.documents.PdfReport;
import it.sweven.blockcovid.blockchain.documents.ReportType;
import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentServiceTest {

  private DocumentService service;
  private String destination_dir;

  @BeforeEach
  void setUp() throws IOException {
    destination_dir = "report_tests" + LocalDateTime.now();
    Files.createDirectory(Path.of(destination_dir));
    service = spy(new DocumentService(destination_dir));
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.deleteIfExists(Path.of(destination_dir));
  }

  @Test
  void generateCleanerReport_reportCorrectlyCreated()
      throws IOException, BadAttributeValueExpException {
    doReturn("pathFile").when(service).initializeReport(any(), eq(ReportType.CLEANER));
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
    doReturn("pathFile").when(service).initializeReport(any(), eq(ReportType.CLEANER));
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
    doReturn("pathFile").when(service).initializeReport(any(), eq(ReportType.USAGE));
    ReservationWithRoom
        reservation1 =
            new ReservationWithRoom(
                "id1",
                "deskId1",
                "room1",
                "username",
                LocalDateTime.now().minusHours(3),
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                true),
        reservation2 =
            new ReservationWithRoom(
                "id2",
                "deskId2",
                "room1",
                "username",
                LocalDateTime.now().minusHours(6),
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(4),
                null,
                false),
        reservation3 =
            new ReservationWithRoom(
                "id2",
                "deskId2",
                "room1",
                "username",
                LocalDateTime.now().minusHours(6),
                LocalDateTime.now().minusHours(2),
                null,
                null,
                false),
        reservation4 =
            new ReservationWithRoom(
                "id2",
                "deskId2",
                "room1",
                "username",
                LocalDateTime.now().minusHours(6),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().minusHours(4),
                null,
                false);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.landscape()).thenReturn(mockReport);
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    assertEquals(
        "pathFile", service.generateUsageReport(List.of(reservation1, reservation2, reservation3)));
    verify(mockReport).create("pathFile");
  }

  @Test
  void generateUsageReport_errorWhileCreatingReport_throwsIOException()
      throws IOException, BadAttributeValueExpException {
    doReturn("pathFile").when(service).initializeReport(any(), eq(ReportType.USAGE));
    ReservationWithRoom
        reservation1 =
            new ReservationWithRoom(
                "id1",
                "deskId1",
                "room1",
                "username",
                LocalDateTime.now().withHour(10),
                LocalDateTime.now().withHour(13),
                LocalDateTime.now().withHour(11),
                LocalDateTime.now().withHour(12),
                true),
        reservation2 =
            new ReservationWithRoom(
                "id2",
                "deskId2",
                "room1",
                "username",
                LocalDateTime.now().withHour(15),
                LocalDateTime.now().withHour(18),
                LocalDateTime.now().withHour(14),
                LocalDateTime.now().withHour(17),
                false);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.landscape()).thenReturn(mockReport);
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
    doReturn("pathFile").when(service).pathReport(any(), any());
    doReturn(false).when(service).fileExists(any());
    doNothing().when(service).createDirectory(any());
    doNothing().when(service).createFile(any());
    assertEquals(
        "pathFile",
        service.initializeReport(LocalDateTime.of(2021, 1, 1, 20, 0), ReportType.CLEANER));
  }

  @Test
  void pathReport() {
    String pathReport = service.pathReport("idPath", ReportType.USAGE);
    assertTrue(pathReport.contains("idPath"));
    assertTrue(pathReport.contains("usage"));
  }

  @Test
  void emptyDestDir() throws IOException {
    assertEquals(Collections.emptyList(), service.getAllReports());
  }

  @Test
  void validFileMeansValidInformation() throws IOException {
    Path newFile = Files.createFile(Path.of(destination_dir + "/report.pdf"));
    ReportInformation info =
        service.getAllReports().stream().findFirst().orElseThrow(IOException::new);
    BasicFileAttributes attributes = Files.readAttributes(newFile, BasicFileAttributes.class);
    assertEquals(info.getCreationDate(), attributes.creationTime());
    assertEquals(info.getRegistrationDate(), attributes.lastModifiedTime());
    Files.deleteIfExists(newFile);
  }

  @Test
  void SetRegisteredPrependsInformation() throws IOException {
    Path newFile = Files.createFile(Path.of(destination_dir + "/report.pdf"));
    service.setAsVerified(newFile.toString());
    Path movedFile = Path.of(destination_dir + "/Registered_" + newFile.getFileName());
    assertFalse(Files.exists(newFile));
    assertTrue(Files.exists(movedFile));
    Files.deleteIfExists(movedFile);
  }
}
