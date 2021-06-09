package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.exceptions.ReportNotFoundException;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.IOException;
import java.nio.file.Path;
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
  private final ReportService reportService;
  private final ReservationService reservationService;
  private final Logger logger = LoggerFactory.getLogger(AutomaticUsageController.class);

  @Autowired
  public AutomaticUsageController(
      ReportService reportService,
      SignRegistrationService signRegistrationService,
      ReservationService reservationService) {
    this.signRegistrationService = signRegistrationService;
    this.reportService = reportService;
    this.reservationService = reservationService;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    ReportInformation information =
        reportService.generateUsageReport(
            reservationService.findByTimeInterval(
                LocalDateTime.now().minusDays(1), LocalDateTime.now()));

    try {
      TransactionReceipt receipt =
          signRegistrationService.registerString(
              reportService.hashOf(Path.of(information.getPath())));
      ReportInformation newInformation =
          reportService.setAsVerified(information.getPath(), receipt.getTransactionHash());
      logger.info(
          "registered report "
              + newInformation
              + " on the blockchain on block "
              + receipt.getBlockNumber().toString());
    } catch (IOException | ReportNotFoundException exception) {
      logger.error(
          "Unable to open file stream for file with information: "
              + information
              + ", nested exception message is: "
              + exception.getMessage());
    }
  }
}
