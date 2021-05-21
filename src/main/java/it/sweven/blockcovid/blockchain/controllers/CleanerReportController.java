package it.sweven.blockcovid.blockchain.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import it.sweven.blockcovid.users.entities.User;
import java.io.IOException;
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

@RestController
public class CleanerReportController implements ReportsController {
  private final RoomService roomService;
  private final DocumentService documentService;
  private final SignRegistrationService signRegistrationService;
  private final Logger logger = LoggerFactory.getLogger(CleanerReportController.class);

  @Autowired
  public CleanerReportController(
      RoomService roomService,
      DocumentService documentService,
      SignRegistrationService signRegistrationService) {
    this.roomService = roomService;
    this.documentService = documentService;
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
      String path = documentService.generateCleanerReport(roomService.getAllRooms());
      logger.info("file saved at path " + path);
      Thread registrationThread =
          new Thread(
              () -> {
                try {
                  signRegistrationService.registerString(documentService.hashOf(path));
                  documentService.setAsVerified(path);
                  logger.info(
                      "successfully registered file " + path + " on the provided blockchain");
                } catch (Exception exception) {
                  logger.error("Unable to open file stream for file at path: " + path);
                }
              });
      registrationThread.start();
      return documentService.readReport(path);
    } catch (IOException e) {
      logger.error(
          "An error occurred while trying to create a new cleaner report: " + e.getMessage());
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the report");
    }
  }
}
