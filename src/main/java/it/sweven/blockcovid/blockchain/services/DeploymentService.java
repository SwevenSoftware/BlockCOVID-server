package it.sweven.blockcovid.blockchain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class DeploymentService {

  private final Web3j connection;
  private final ContractGasProvider gasProvider;
  private final Credentials account;
  private final Logger logger = LoggerFactory.getLogger(DeploymentService.class);

  @Autowired
  public DeploymentService(Web3j connection, ContractGasProvider gasProvider, Credentials account) {
    this.connection = connection;
    this.gasProvider = gasProvider;
    this.account = account;
  }

  public DocumentContract loadContract(String contractAddress) throws Exception {
    DocumentContract loaded =
        DocumentContract.load(contractAddress, connection, account, gasProvider);
    logger.info("Loaded contract " + loaded.getContractAddress());
    return loaded;
  }

  public DocumentContract deployContract(Credentials account) throws Exception {
    DocumentContract deployed = DocumentContract.deploy(connection, account, gasProvider).send();
    logger.info("Deployed new contract at address " + deployed.getContractAddress());
    return deployed;
  }
}
