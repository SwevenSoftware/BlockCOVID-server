package it.sweven.blockcovid.blockchain.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.documents.PdfReport;
import it.sweven.blockcovid.blockchain.documents.ReportType;
import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.exceptions.ReportNotFoundException;
import it.sweven.blockcovid.blockchain.repositories.ReportInformationRepository;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.rooms.entities.Room;
import it.sweven.blockcovid.rooms.entities.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.management.BadAttributeValueExpException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportServiceTest {

  private ReportService service;
  private ReportInformationRepository repository;
  private String destination_dir;

  @BeforeEach
  void setUp() throws IOException {
    Files.createFile(Path.of("pathFile"));
    destination_dir = "report_tests" + LocalDateTime.now();
    repository = mock(ReportInformationRepository.class);
    doAnswer(invocationOnMock -> invocationOnMock.getArgument(0)).when(repository).save(any());
    Files.createDirectory(Path.of(destination_dir));
    DeploymentInformation mockInfo = mock(DeploymentInformation.class);
    when(mockInfo.getContract()).thenReturn("contract");
    when(mockInfo.getAccount()).thenReturn("account");
    service = spy(new ReportService(destination_dir, repository, mockInfo));
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.deleteIfExists(Path.of("pathFile"));
    Files.walk(Path.of(destination_dir))
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach(File::delete);
  }

  @Test
  void generateCleanerReport_reportCorrectlyCreated()
      throws IOException, BadAttributeValueExpException {
    doReturn(Path.of("pathFile")).when(service).initializeReport(any(), eq(ReportType.CLEANER));
    Room mockRoom1 = mock(Room.class), mockRoom2 = mock(Room.class);
    when(mockRoom1.getName()).thenReturn("room1");
    when(mockRoom1.getRoomStatus()).thenReturn(Status.CLEAN);
    when(mockRoom1.getLastCleaned()).thenReturn(LocalDateTime.MIN);
    when(mockRoom1.getLastCleaner()).thenReturn("cleaner");
    when(mockRoom2.getName()).thenReturn("room2");
    when(mockRoom2.getRoomStatus()).thenReturn(Status.DIRTY);
    when(mockRoom2.getLastCleaned()).thenReturn(null);
    when(mockRoom2.getLastCleaner()).thenReturn(null);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderInfo(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    ReportInformation information = service.generateCleanerReport(List.of(mockRoom1, mockRoom2));
    assertNull(information.getRegistrationDate());
    assertNull(information.getTransactionHash());
    assertFalse(information.getRegistered());
    verify(mockReport).create(Path.of("pathFile"));
  }

  @Test
  void generateCleanerReport_errorWhileCreatingReport_throwsIOException()
      throws IOException, BadAttributeValueExpException {
    doReturn(Path.of("pathFile")).when(service).initializeReport(any(), eq(ReportType.CLEANER));
    Room mockRoom1 = mock(Room.class), mockRoom2 = mock(Room.class);
    when(mockRoom1.getName()).thenReturn("room1");
    when(mockRoom1.getRoomStatus()).thenReturn(Status.CLEAN);
    when(mockRoom2.getName()).thenReturn("room2");
    when(mockRoom2.getRoomStatus()).thenReturn(Status.DIRTY);
    PdfReport mockReport = mock(PdfReport.class);
    doReturn(mockReport).when(service).createNewReport();
    when(mockReport.setTitle(any())).thenReturn(mockReport);
    when(mockReport.setTimestamp(any())).thenReturn(mockReport);
    when(mockReport.setHeaderInfo(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    doThrow(new BadAttributeValueExpException(null)).when(mockReport).create(any());
    assertThrows(
        IOException.class, () -> service.generateCleanerReport(List.of(mockRoom1, mockRoom2)));
  }

  @Test
  void generateUsageReport_reportCorrectlyCreated()
      throws BadAttributeValueExpException, IOException {
    doReturn(Path.of("pathFile")).when(service).initializeReport(any(), eq(ReportType.USAGE));
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
    when(mockReport.setHeaderInfo(any())).thenReturn(mockReport);
    when(mockReport.setHeaderTable(any())).thenReturn(mockReport);
    when(mockReport.addRowTable(any())).thenReturn(mockReport);
    ReportInformation information =
        service.generateUsageReport(List.of(reservation1, reservation2, reservation3));
    assertFalse(information.getRegistered());
    assertNull(information.getTransactionHash());
    assertNull(information.getRegistrationDate());
    verify(mockReport).create(Path.of("pathFile"));
  }

  @Test
  void generateUsageReport_errorWhileCreatingReport_throwsIOException()
      throws IOException, BadAttributeValueExpException {
    doReturn(Path.of("pathFile")).when(service).initializeReport(any(), eq(ReportType.USAGE));
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
    when(mockReport.setHeaderInfo(any())).thenReturn(mockReport);
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
    Path file = service.initializeReport(LocalDateTime.of(2021, 1, 1, 20, 0), ReportType.CLEANER);
    assertEquals("pathFile", file.getFileName().toString());
  }

  @Test
  void pathReport() {
    String pathReport = service.pathReport("idPath", ReportType.USAGE);
    assertTrue(pathReport.contains("idPath"));
    assertTrue(pathReport.contains("usage"));
  }

  @Test
  void findReport_correctlyFound() throws IOException {
    doReturn(true).when(service).validFilename("reportFilename.pdf");
    doReturn(true).when(service).fileExists(contains("reportFilename.pdf"));
    byte[] expectedBytes = "correct report".getBytes();
    doReturn(expectedBytes).when(service).readReport(any());
    assertEquals(expectedBytes, service.findReport("reportFilename.pdf"));
  }

  @Test
  void findReport_invalidFilename() {
    doReturn(false).when(service).validFilename(any());
    assertThrows(IllegalArgumentException.class, () -> service.findReport("reportFilename.pdf"));
  }

  @Test
  void findReport_fileNotFound() throws IOException {
    doReturn(true).when(service).validFilename("reportFilename.pdf");
    doReturn(false).when(service).fileExists(any());
    assertThrows(NoSuchFileException.class, () -> service.findReport("reportFilename.pdf"));
  }

  @Test
  void findReport_fileNotRead() throws IOException {
    doReturn(true).when(service).validFilename("reportFilename.pdf");
    doReturn(true).when(service).fileExists(contains("reportFilename.pdf"));
    doThrow(new IOException()).when(service).readReport(any());
    assertThrows(IOException.class, () -> service.findReport("reportFilename.pdf"));
  }

  @Test
  void validFilename() {
    assertTrue(service.validFilename("Registered_Report_usage_20210101_103050.pdf"));
    assertTrue(service.validFilename("Registered_Report_cleaner_20210101_103050.pdf"));
    assertTrue(service.validFilename("Report_usage_20210101_103050.pdf"));
    assertTrue(service.validFilename("Report_cleaner_20210101_103050.pdf"));
    assertFalse(service.validFilename("Report_20210101_103050.pdf"));
    assertFalse(service.validFilename("Registered_Report_20210101_103050.pdf"));
    assertFalse(service.validFilename("Registered_Report_20210101.pdf"));
    assertFalse(service.validFilename("Registered_Report.pdf"));
    assertFalse(service.validFilename("Report.pdf"));
    assertFalse(service.validFilename("Registered_Report_usage_20210101_103050"));
    assertFalse(service.validFilename("Report_cleaner_20210101_103050"));
    assertFalse(service.validFilename("Report_cleaner_20210101_103050.txt"));
  }

  @Test
  void emptyDestDir() throws IOException {
    assertEquals(Collections.emptyList(), service.getAllReports());
  }

  @Test
  void setAsVerifiedWorks() throws ReportNotFoundException {
    String txHash = "txHash";
    String path = "pathFile";
    when(repository.findByName(any()))
        .thenReturn(Optional.of(new ReportInformation("name", null, null, null, null, null, null)));
    ReportInformation information = service.setAsVerified(path, txHash);
    assertEquals(txHash, information.getTransactionHash());
    assertNotNull(information.getRegistrationDate());
    assertTrue(information.getRegistered());
  }

  @Test
  void setAsVerifiedReportNotFound() {
    String txHash = "txHash";
    String path = "pathFile";
    when(repository.findByName(any())).thenReturn(Optional.empty());
    assertThrows(ReportNotFoundException.class, () -> service.setAsVerified(path, txHash));
  }
}
