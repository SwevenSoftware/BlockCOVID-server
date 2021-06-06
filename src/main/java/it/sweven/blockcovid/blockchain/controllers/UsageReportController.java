package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.exceptions.ReportNotFoundException;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@RestController
public class UsageReportController implements ReportsController {
  private final ReservationService reservationService;
  private final ReportService reportService;
  private final SignRegistrationService signRegistrationService;
  private final Logger logger = LoggerFactory.getLogger(UsageReportController.class);

  @Autowired
  public UsageReportController(
      ReservationService reservationService,
      ReportService reportService,
      SignRegistrationService signRegistrationService) {
    this.reservationService = reservationService;
    this.reportService = reportService;
    this.signRegistrationService = signRegistrationService;
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
      ReportInformation information =
          reportService.generateUsageReport(reservationService.findByTimeInterval(from, to));
      logger.info("New usage report generated at path " + information.getPath());
      Thread registrationThread =
          new Thread(
              () -> {
                try {
                  TransactionReceipt receipt =
                      signRegistrationService.registerString(
                          reportService.hashOf(Path.of(information.getPath())));
                  reportService.setAsVerified(information.getPath(), receipt.getTransactionHash());
                  logger.info(
                      "successfully registered the file "
                          + information.getName()
                          + " on the provided blockchain");
                } catch (Exception exception) {
                  logger.error(
                      "Unable to open file stream for file at path: " + information.getPath());
                } catch (ReportNotFoundException e) {
                  logger.error("Unable to set file " + information.getPath() + " as registered");
                }
              });
      registrationThread.start();
      return reportService.readReport(Path.of(information.getPath()));
    } catch (IOException e) {
      logger.error("An error occurred while trying to create a report: " + e.getMessage());
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the report");
    }
  }
}
