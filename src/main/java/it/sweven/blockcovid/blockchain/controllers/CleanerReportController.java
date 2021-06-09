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
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@RestController
public class CleanerReportController implements ReportsController {
  private final RoomService roomService;
  private final ReportService reportService;
  private final SignRegistrationService signRegistrationService;
  private final Logger logger = LoggerFactory.getLogger(CleanerReportController.class);

  @Autowired
  public CleanerReportController(
      RoomService roomService,
      ReportService reportService,
      SignRegistrationService signRegistrationService) {
    this.roomService = roomService;
    this.reportService = reportService;
    this.signRegistrationService = signRegistrationService;
  }

  @GetMapping(value = "/cleaner", produces = MediaType.APPLICATION_PDF_VALUE)
  @ResponseBody
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Report listing rooms' cleaning status"),
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
  public byte[] report(@Parameter(hidden = true) @AuthenticationPrincipal User submitter) {
    try {
      ReportInformation information =
          reportService.generateCleanerReport(roomService.getAllRooms());
      logger.info("file saved at path " + information.getPath());
      Thread registrationThread =
          new Thread(
              () -> {
                try {
                  TransactionReceipt receipt =
                      signRegistrationService.registerString(
                          reportService.hashOf(Path.of(information.getPath())));
                  reportService.setAsVerified(information.getPath(), receipt.getTransactionHash());
                  logger.info(
                      "successfully registered file "
                          + information.getName()
                          + " on the provided blockchain");
                } catch (Exception exception) {
                  logger.error(
                      "Unable to open file stream for file at path: " + information.getName());
                } catch (ReportNotFoundException e) {
                  logger.error("Unable to find report " + information.getName() + " in repository");
                }
              });
      registrationThread.start();
      return reportService.readReport(Path.of(information.getPath()));
    } catch (IOException e) {
      logger.error(
          "An error occurred while trying to create a new cleaner report: " + e.getMessage());
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the report");
    }
  }
}
