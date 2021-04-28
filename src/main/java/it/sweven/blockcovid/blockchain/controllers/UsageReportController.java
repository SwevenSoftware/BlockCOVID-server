package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.documentcontract.DocumentContract;

@RestController
public class UsageReportController implements ReportsController {
  private final ReservationService reservationService;
  private final DocumentService documentService;
  private final DocumentContract contract;
  private final Logger logger = LoggerFactory.getLogger(UsageReportController.class);

  @Autowired
  public UsageReportController(
      ReservationService reservationService,
      DocumentService documentService,
      DocumentContract contract) {
    this.reservationService = reservationService;
    this.documentService = documentService;
    this.contract = contract;
  }

  @GetMapping(value = "/usage", produces = MediaType.APPLICATION_PDF_VALUE)
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Report listing reservations' usage status"),
    @ApiResponse(
        responseCode = "403",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = void.class))),
    @ApiResponse(
        responseCode = "500",
        description = "An error occurred while processing the report",
        content = @Content(schema = @Schema(implementation = void.class)))
  })
  @PreAuthorize("#submitter.isAdmin()")
  public byte[] report(
      @Parameter(hidden = true) @AuthenticationPrincipal User submitter,
      @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
    try {
      String path =
          documentService.generateUsageReport(reservationService.findByTimeInterval(from, to));
      logger.info("file saved at path " + path);
      Thread registrationThread =
          new Thread(
              () -> {
                try {
                  deploymentService.registerReport(contract, Path.of(path));
                  documentService.setAsVerified(path);
                } catch (Exception exception) {
                  logger.error("Unable to open file stream for file at path: " + path);
                }
              });
      registrationThread.start();
      return documentService.readReport(path);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the report");
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while registering the document on the provided blockchain");
    }
  }
}
