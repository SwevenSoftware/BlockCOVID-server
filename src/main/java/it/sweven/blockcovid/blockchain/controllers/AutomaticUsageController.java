package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.IOException;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class AutomaticUsageController {
  private final SignRegistrationService signRegistrationService;
  private final DocumentService documentService;
  private final ReservationService reservationService;
  private final Logger logger = LoggerFactory.getLogger(AutomaticUsageController.class);

  @Autowired
  public AutomaticUsageController(
      DocumentService documentService,
      SignRegistrationService signRegistrationService,
      ReservationService reservationService) {
    this.signRegistrationService = signRegistrationService;
    this.documentService = documentService;
    this.reservationService = reservationService;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath =
        documentService.generateUsageReport(
            reservationService.findByTimeInterval(
                LocalDateTime.now().minusDays(1), LocalDateTime.now()));

    try {
      TransactionReceipt receipt =
          signRegistrationService.registerString(documentService.hashOf(savedPath));
      String newPath = documentService.setAsVerified(savedPath);
      logger.info(
          "registered file "
              + savedPath
              + " (now "
              + newPath
              + ") on the blockchain on block "
              + receipt.getBlockNumber().toString());
    } catch (IOException exception) {
      logger.error("Unable to open file stream for file at path: " + savedPath);
    }
  }
}
