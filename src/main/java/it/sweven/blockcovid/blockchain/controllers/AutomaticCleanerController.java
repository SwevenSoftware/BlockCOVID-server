package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.blockchain.services.SignRegistrationService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class AutomaticCleanerController {
  private final SignRegistrationService signRegistrationService;
  private final DocumentService documentService;
  private final RoomService roomService;
  private final Logger logger = LoggerFactory.getLogger(AutomaticCleanerController.class);

  @Autowired
  public AutomaticCleanerController(
      SignRegistrationService signRegistrationService,
      DocumentService documentService,
      RoomService roomService) {
    this.signRegistrationService = signRegistrationService;
    this.documentService = documentService;
    this.roomService = roomService;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath = documentService.generateCleanerReport(roomService.getAllRooms());
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
      logger.error(
          "Unable to open file stream for file at path: "
              + savedPath
              + ", nested exception message is: "
              + exception.getMessage());
    }
  }
}
