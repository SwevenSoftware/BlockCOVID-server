package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class GetReportControllerTest {

  private DocumentService service;
  private GetReportController controller;

  @BeforeEach
  void setUp() {
    service = mock(DocumentService.class);
    controller = new GetReportController(service);
  }

  @Test
  void getReport_reportRetrievedCorrectly() throws IOException {
    byte[] expectedReturn = "correct report".getBytes();
    when(service.findReport("reportName")).thenReturn(expectedReturn);
    assertEquals(expectedReturn, controller.getReport(mock(User.class), "reportName"));
  }

  @Test
  void getReport_fileNotFound() throws IOException {
    when(service.findReport(any())).thenThrow(new NoSuchFileException(""));
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.getReport(mock(User.class), "reportName"));
    assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
  }

  @Test
  void getReport_fileNotRead() throws IOException {
    when(service.findReport(any())).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.getReport(mock(User.class), "reportName"));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }

  @Test
  void getReport_filenameNotValid() throws IOException {
    when(service.findReport(any())).thenThrow(new IllegalArgumentException());
    ResponseStatusException thrown =
        assertThrows(
            ResponseStatusException.class,
            () -> controller.getReport(mock(User.class), "reportName"));
    assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
  }
}
