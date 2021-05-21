package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class GetReportController implements ReportsController {
  private final DocumentService service;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public GetReportController(DocumentService service) {
    this.service = service;
  }

  @GetMapping(value = "/report/{reportName}", produces = MediaType.APPLICATION_PDF_VALUE)
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Return requested report"),
    @ApiResponse(
        responseCode = "400",
        description = "Filename syntax not valid",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Report not found",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "500",
        description = "An error occurred while retrieving the report",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isAdmin()")
  public byte[] getReport(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @PathVariable String reportName) {
    try {
      return service.findReport(reportName);
    } catch (NoSuchFileException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file " + reportName + " not found");
    } catch (IOException e) {
      logger.warn(
          "An error occurred while trying to read file with name "
              + reportName
              + ": "
              + e.getMessage());
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while reading file " + reportName);
    } catch (IllegalArgumentException e) {
      logger.warn(
          "An error occurred while trying to read file with name "
              + reportName
              + ", not a valid file name");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, reportName + " is not a valid filename");
    }
  }
}
