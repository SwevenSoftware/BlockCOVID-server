package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.assemblers.RegistrationInformationAssembler;
import it.sweven.blockcovid.blockchain.dto.RegistrationInformation;
import it.sweven.blockcovid.blockchain.exceptions.InvalidHash;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.users.entities.User;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
  private final RegistrationInformationAssembler assembler;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public VerificationController(
      SignRegistrationService signRegistrationService, RegistrationInformationAssembler assembler) {
    this.signRegistrationService = signRegistrationService;
    this.assembler = assembler;
  }

  @PostMapping("verify")
  @PreAuthorize("#submitter.isAdmin()")
  public EntityModel<RegistrationInformation> verify(
      @AuthenticationPrincipal User submitter, @RequestBody String hash) {
    try {
      BigInteger registrationTime = signRegistrationService.verifyHash(hash);
      return assembler.toModel(
          new RegistrationInformation(
              LocalDateTime.ofEpochSecond(registrationTime.longValue(), 0, ZoneOffset.UTC)));
    } catch (InvalidHash invalidHash) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hash not found on the blockchain");
    } catch (Exception exception) {
      logger.error(
          "Unable to talk to the provided network, "
              + "future requests might lead to more errors, check your configuration!");
      logger.error("Further information: " + exception.getMessage());
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Unable to talk to the blockchain");
    }
  }
}
