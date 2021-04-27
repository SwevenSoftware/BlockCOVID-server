package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import it.sweven.blockcovid.blockchain.services.DocumentContractService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.FileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class BlockchainController {
  private final BlockchainService blockchainService;
  private final DocumentContractService documentContractService;
  private final DocumentService documentService;
  private final RoomService roomService;
  private final BlockchainDeploymentInformation deploymentInformation;
  private final Logger logger = LoggerFactory.getLogger(BlockchainController.class);

  @Autowired
  public BlockchainController(
      BlockchainService blockchainService,
      DocumentContractService documentContractService,
      DocumentService documentService,
      RoomService roomService,
      BlockchainDeploymentInformation deploymentInformation) {
    this.blockchainService = blockchainService;
    this.documentContractService = documentContractService;
    this.documentService = documentService;
    this.roomService = roomService;
    this.deploymentInformation = deploymentInformation;
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void run() throws Exception {
    String savedPath = documentService.generateCleanerReport(roomService.getAllRooms());
    DocumentContract contract =
        documentContractService.getContractByAccountAndNetwork(
            deploymentInformation.getAccount(), deploymentInformation.getNetwork());
    TransactionReceipt transactionReceipt =
        blockchainService.registerReport(contract, new FileInputStream(savedPath));
    logger.info("saved report at " + savedPath + " on block " + transactionReceipt.getBlockHash());
  }
}
