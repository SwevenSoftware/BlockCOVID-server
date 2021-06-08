package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.reservations.dto.ReservationWithRoom;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class UsageReportControllerTest {

  private ReservationService reservationService;
  private ReportService reportService;
  private UsageReportController controller;
  private ReportInformation information;

  @BeforeEach
  void setUp() {
    reservationService = mock(ReservationService.class);
    reportService = mock(ReportService.class);
    SignRegistrationService signRegistrationService = mock((SignRegistrationService.class));
    when(reservationService.findByTimeInterval(any(), any())).thenReturn(Collections.emptyList());
    information = mock(ReportInformation.class);
    when(information.getPath()).thenReturn("path");
    controller =
        new UsageReportController(reservationService, reportService, signRegistrationService);
  }

  @Test
  void report_reportCorrectlyGenerated() throws Exception {
    LocalDateTime from = LocalDateTime.MIN.withHour(13), to = LocalDateTime.MIN.withHour(19);
    List<ReservationWithRoom> listReservations =
        List.of(mock(ReservationWithRoom.class), mock(ReservationWithRoom.class));
    when(reservationService.findByTimeInterval(from, to)).thenReturn(listReservations);
    when(reportService.generateUsageReport(listReservations)).thenReturn(information);
    Files.deleteIfExists(Path.of("pathReport"));
    Files.createFile(Path.of("pathReport"));
    byte[] expectedBytes = "correct report".getBytes();
    when(reportService.readReport(any())).thenReturn(expectedBytes);
    assertEquals(expectedBytes, controller.report(mock(User.class), from, to));
    Files.deleteIfExists(Path.of("pathReport"));
  }

  @Test
  void report_reportGenerationFails_throwsResponseStatusException() throws IOException {
    when(reportService.generateUsageReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_contractCreationFails_throwsResponseStatusException() throws Exception {
    when(reportService.generateUsageReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void report_reportRegistrationFails_doesNotThrow() throws Exception {
    when(reportService.generateUsageReport(any())).thenReturn(information);
    when(reportService.hashOf(any())).thenThrow(new IOException());
    assertDoesNotThrow(
        () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
  }

  @Test
  void report_reportReadFails_throwsResponseStatusException() throws IOException {
    when(reportService.generateUsageReport(any())).thenReturn(information);
    when(reportService.readReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.report(mock(User.class), LocalDateTime.now(), LocalDateTime.now()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
