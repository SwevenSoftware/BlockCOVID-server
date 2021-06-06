package it.sweven.blockcovid.blockchain.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import it.sweven.blockcovid.blockchain.exceptions.InvalidHash;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.users.entities.User;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class VerificationController implements ReportsController {
  private final SignRegistrationService signRegistrationService;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public VerificationController(SignRegistrationService signRegistrationService) {
    this.signRegistrationService = signRegistrationService;
  }

  @PostMapping("verify")
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<LocalDateTime> verify(
      @AuthenticationPrincipal User submitter, @RequestBody String hash) {
    try {
      LocalDateTime registrationTime = signRegistrationService.verifyHash(hash);
      return EntityModel.of(
          registrationTime,
          linkTo(methodOn(UsageReportController.class).report(null, null, null))
              .withRel("new_usage_report"),
          linkTo(methodOn(ListReportsController.class).listReports(null))
              .withRel("list_all_available_reports"),
          linkTo(methodOn(CleanerReportController.class).report(null))
              .withRel("new_cleaner_report"));
    } catch (InvalidHash invalidHash) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hash not found on the blockchain");
    } catch (Exception exception) {
      logger.error(
          "Unable to talk to the provided network, "
              + "future requests might lead to more errors, check your configuration!");
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Unable to talk to the blockchain");
    }
  }
}
