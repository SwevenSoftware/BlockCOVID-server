package it.sweven.blockcovid.controllers.blockchain;

import it.sweven.blockcovid.services.BlockchainDeploymentInformationsService;
import it.sweven.blockcovid.services.BlockchainService;
import it.sweven.blockcovid.services.DocumentService;
import it.sweven.blockcovid.services.RoomService;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class BlockchainController {
  private final BlockchainService blockchainService;
  private final BlockchainDeploymentInformationsService blockchainDeploymentInformationsService;
  private final DocumentService documentService;
  private final RoomService roomService;
  private final Credentials accountCredentials;
  private final Logger logger = LoggerFactory.getLogger(BlockchainController.class);

  @Autowired
  public BlockchainController(
      BlockchainService blockchainService,
      BlockchainDeploymentInformationsService blockchainDeploymentInformationsService,
      DocumentService documentService,
      RoomService roomService,
      Credentials accountCredentials) {
    this.blockchainService = blockchainService;
    this.blockchainDeploymentInformationsService = blockchainDeploymentInformationsService;
    this.documentService = documentService;
    this.roomService = roomService;
    this.accountCredentials = accountCredentials;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath = documentService.generateCleanerReport(roomService.getAllRooms());
    DocumentContract contract =
        blockchainDeploymentInformationsService.getContractByAccount(accountCredentials);
    TransactionReceipt transactionReceipt =
        blockchainService.registerReport(contract, new FileInputStream(savedPath));
    logger.info("saved report at " + savedPath + " on block " + transactionReceipt.getBlockHash());
  }
}
