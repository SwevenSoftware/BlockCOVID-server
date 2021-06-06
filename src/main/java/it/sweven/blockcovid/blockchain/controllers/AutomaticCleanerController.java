package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import it.sweven.blockcovid.blockchain.exceptions.ReportNotFoundException;
import it.sweven.blockcovid.blockchain.services.ReportService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class AutomaticCleanerController {
  private final SignRegistrationService signRegistrationService;
  private final ReportService reportService;
  private final RoomService roomService;
  private final Logger logger = LoggerFactory.getLogger(AutomaticCleanerController.class);

  @Autowired
  public AutomaticCleanerController(
      SignRegistrationService signRegistrationService,
      ReportService reportService,
      RoomService roomService) {
    this.signRegistrationService = signRegistrationService;
    this.reportService = reportService;
    this.roomService = roomService;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    ReportInformation information = reportService.generateCleanerReport(roomService.getAllRooms());
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
              + receipt.getBlockNumber());
    } catch (IOException | ReportNotFoundException exception) {
      logger.error(
          "Unable to open file stream for file with information: "
              + information
              + ", nested exception message is: "
              + exception.getMessage());
    }
  }
}
