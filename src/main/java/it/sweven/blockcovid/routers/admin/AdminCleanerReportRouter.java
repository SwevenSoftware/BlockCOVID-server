package it.sweven.blockcovid.routers.admin;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
import java.io.IOException;
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
public class AdminCleanerReportRouter implements AdminRouter {
  private final RoomService roomService;
  private final DocumentService documentService;

  @Autowired
  public AdminCleanerReportRouter(RoomService roomService, DocumentService documentService) {
    this.roomService = roomService;
    this.documentService = documentService;
  }

  @GetMapping(value = "/report/cleaner", produces = MediaType.APPLICATION_PDF_VALUE)
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
  public byte[] report(@AuthenticationPrincipal User submitter) {
    try {
      String path = documentService.generateCleanerReport(roomService.getAllRooms());
      return documentService.readReport(path);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the report");
    }
  }
}
