package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.blockchain.assemblers.ReportInformationAssembler;
import it.sweven.blockcovid.blockchain.dto.ReportInformation;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ListReportsController implements ReportsController {

  private final DocumentService documentService;
  private final ReportInformationAssembler assembler;

  @Autowired
  public ListReportsController(
      DocumentService documentService, ReportInformationAssembler assembler) {
    this.documentService = documentService;
    this.assembler = assembler;
  }

  @GetMapping("all")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved the list of all available reports"),
    @ApiResponse(
        responseCode = "500",
        description = "An error occurred while listing all reports, contact the maintainer",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isAdmin()")
  @ResponseBody
  public CollectionModel<EntityModel<ReportInformation>> listReports(
      @AuthenticationPrincipal User submitter) {
    try {
      return assembler.toCollectionModel(documentService.getAllReports());
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while listing all reports, contact the maintainer");
    }
  }
}
