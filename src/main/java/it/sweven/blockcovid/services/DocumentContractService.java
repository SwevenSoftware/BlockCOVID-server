package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.BlockchainDeploymentInformations;
import it.sweven.blockcovid.repositories.DocumentContractRepository;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class DocumentContractService {
  private final DocumentContractRepository documentContractRepository;
  private final Web3j connection;
  private final ContractGasProvider gasProvider;
  private final Logger logger = LoggerFactory.getLogger(DocumentContractService.class);

  @Autowired
  public DocumentContractService(
      DocumentContractRepository documentContractRepository,
      Web3j connection,
      ContractGasProvider gasProvider) {
    this.documentContractRepository = documentContractRepository;
    this.connection = connection;
    this.gasProvider = gasProvider;
  }

  public DocumentContract getContractByAccount(Credentials accountCredentials) throws Exception {
    try {
      String contractAddress =
          documentContractRepository
              .findByAccount(accountCredentials)
              .orElseThrow(NoSuchElementException::new)
              .getAddress();
      return DocumentContract.load(contractAddress, connection, accountCredentials, gasProvider);
    } catch (NoSuchElementException exception) {
      DocumentContract deployed = deployContract(accountCredentials);
      logger.info(
          "No old deployed contract found, deployed new contract at "
              + deployed.getContractAddress());
      documentContractRepository.save(
          new BlockchainDeploymentInformations(accountCredentials, deployed.getContractAddress()));
      return deployed;
    }
  }

  private DocumentContract deployContract(Credentials account) throws Exception {
    return DocumentContract.deploy(connection, account, gasProvider).send();
  }
}
