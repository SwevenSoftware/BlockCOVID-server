package it.sweven.blockcovid.blockchain.controllers;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.services.DeploymentInformationService;
import it.sweven.blockcovid.blockchain.services.DeploymentService;
import it.sweven.blockcovid.blockchain.services.DocumentService;
import it.sweven.blockcovid.rooms.services.RoomService;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Controller
public class BlockchainController {
  private final DeploymentService deploymentService;
  private final DeploymentInformationService deploymentInformationService;
  private final DocumentService documentService;
  private final RoomService roomService;
  private final BlockchainDeploymentInformation deploymentInformation;
  private final Logger logger = LoggerFactory.getLogger(BlockchainController.class);

  @Autowired
  public BlockchainController(
      DeploymentService deploymentService,
      DeploymentInformationService deploymentInformationService,
      DocumentService documentService,
      RoomService roomService,
      BlockchainDeploymentInformation deploymentInformation) {
    this.deploymentService = deploymentService;
    this.deploymentInformationService = deploymentInformationService;
    this.documentService = documentService;
    this.roomService = roomService;
    this.deploymentInformation = deploymentInformation;
  }

  @Scheduled(cron = "0 0 * * * *")
  public void run() throws Exception {
    String savedPath = documentService.generateCleanerReport(roomService.getAllRooms());
    try {
      DocumentContract contract = deploymentService.loadContract(deploymentInformation);
      TransactionReceipt receipt =
          deploymentService.registerReport(contract, new FileInputStream(savedPath));
      String newPath = documentService.setAsVerified(savedPath);
      logger.info(
          "registered file "
              + savedPath
              + " (now "
              + newPath
              + ") on the blockchain "
              + deploymentInformation.getNetwork()
              + " on block "
              + receipt.getBlockNumber().toString());
    } catch (IOException exception) {
      logger.error("Unable to open file stream for file at path: " + savedPath);
    } catch (Exception exception) {
      logger.error("Invalid deployment information: " + deploymentInformation.toString());
    }
  }
}
