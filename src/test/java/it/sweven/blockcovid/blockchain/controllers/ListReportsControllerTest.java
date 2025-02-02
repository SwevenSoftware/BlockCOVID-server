package it.sweven.blockcovid.blockchain.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.blockchain.assemblers.ReportInformationAssembler;
import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ListReportsControllerTest {

  private ReportService reportService;
  private ListReportsController controller;
  private ReportInformation content;

  @BeforeEach
  void setUp() throws IOException {
    reportService = mock(ReportService.class);
    content = mock(ReportInformation.class);
    when(reportService.getAllReports()).thenReturn(List.of(content));
    ReportInformationAssembler reportInformationAssembler = mock(ReportInformationAssembler.class);
    doAnswer(invocationOnMock -> EntityModel.of(invocationOnMock.getArgument(0)))
        .when(reportInformationAssembler)
        .toModel(any());
    doAnswer(invocationOnMock -> CollectionModel.of(invocationOnMock.getArgument(0)))
        .when(reportInformationAssembler)
        .toCollectionModel(anyIterable());

    controller = new ListReportsController(reportService, reportInformationAssembler);
  }

  @Test
  void happyPath() {
    assertTrue(controller.listReports(mock(User.class)).getContent().contains(content));
  }

  @Test
  void documentServiceFails_throwsResponseStatusException() throws IOException {
    when(reportService.getAllReports()).thenThrow(new IOException());
    ResponseStatusException thrown =
        assertThrows(ResponseStatusException.class, () -> controller.listReports(mock(User.class)));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, thrown.getStatus());
  }
}
