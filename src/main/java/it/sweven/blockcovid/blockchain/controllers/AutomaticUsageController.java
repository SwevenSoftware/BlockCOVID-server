package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.services.DeploymentService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class AutomaticUsageController {
  private final DeploymentService deploymentService;
  private final DocumentService documentService;
  private final ReservationService reservationService;
  private final DocumentContract contract;
  private final Logger logger = LoggerFactory.getLogger(AutomaticUsageController.class);

  @Autowired
  public AutomaticUsageController(
      DeploymentService deploymentService,
      DocumentService documentService,
      ReservationService reservationService,
      DocumentContract contract) {
    this.deploymentService = deploymentService;
    this.documentService = documentService;
    this.reservationService = reservationService;
    this.contract = contract;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath =
        documentService.generateUsageReport(
            reservationService.findByTimeInterval(
                LocalDateTime.now().minusDays(1), LocalDateTime.now()));

    try {
      TransactionReceipt receipt = deploymentService.registerReport(contract, Path.of(savedPath));
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
