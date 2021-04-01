package it.sweven.blockcovid.controllers;

import it.sweven.blockcovid.services.BlockchainService;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class BlockchainController {
  private final BlockchainService blockchainService;
  private final DocumentService documentService;
  private final RoomService roomService;
  private final Logger logger;

  @Autowired
  public BlockchainController(
      BlockchainService blockchainService,
      DocumentService documentService,
      RoomService roomService,
      Logger logger) {
    this.blockchainService = blockchainService;
    this.documentService = documentService;
    this.roomService = roomService;
    this.logger = logger;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() {
    try {
      String savedPath = documentService.generateCleanerReport(roomService.getAllRooms());
      TransactionReceipt transactionReceipt =
          blockchainService.registerReport(new FileInputStream(savedPath));
      logger.info(
          "saved report at \"" + savedPath + " on block " + transactionReceipt.getBlockHash());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
