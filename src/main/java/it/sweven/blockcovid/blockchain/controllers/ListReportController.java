package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ListReportController implements ReportsController {

  private final DocumentService documentService;

  @Autowired
  public ListReportController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @GetMapping("all")
  public CollectionModel<EntityModel<ReportInformation>> listReports(
      @AuthenticationPrincipal User submitter) {
    try {

    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while listing all reports, contact the mantainer");
    }
  }
}
