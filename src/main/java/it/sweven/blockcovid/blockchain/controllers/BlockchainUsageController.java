package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.reservations.servicies.ReservationService;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class BlockchainUsageController {
  private final BlockchainService blockchainService;
  private final DocumentContractService documentContractService;
  private final DocumentService documentService;
  private final ReservationService reservationService;
  private final Credentials accountCredentials;
  private final Logger logger = LoggerFactory.getLogger(BlockchainCleanerController.class);

  @Autowired
  public BlockchainUsageController(
      BlockchainService blockchainService,
      DocumentContractService documentContractService,
      DocumentService documentService,
      ReservationService reservationService,
      Credentials accountCredentials) {
    this.blockchainService = blockchainService;
    this.documentContractService = documentContractService;
    this.documentService = documentService;
    this.reservationService = reservationService;
    this.accountCredentials = accountCredentials;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath =
        documentService.generateUsageReport(
            reservationService.findByTimeInterval(
                LocalDateTime.now().minusDays(1), LocalDateTime.now()));
    DocumentContract contract = documentContractService.getContractByAccount(accountCredentials);
    TransactionReceipt transactionReceipt =
        blockchainService.registerReport(contract, new FileInputStream(savedPath));
    logger.info("saved report at " + savedPath + " on block " + transactionReceipt.getBlockHash());
  }
}
