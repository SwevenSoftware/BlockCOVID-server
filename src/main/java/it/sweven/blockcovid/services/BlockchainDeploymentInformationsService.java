package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.BlockchainDeploymentInformations;
import it.sweven.blockcovid.repositories.BlockchainDeploymentInformationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class BlockchainDeploymentInformationsService {
  private final BlockchainDeploymentInformationsRepository
      blockchainDeploymentInformationsRepository;
  private final Web3j connection;
  private final ContractGasProvider gasProvider;

  @Autowired
  public BlockchainDeploymentInformationsService(
      BlockchainDeploymentInformationsRepository blockchainDeploymentInformationsRepository,
      Web3j connection,
      ContractGasProvider gasProvider) {
    this.blockchainDeploymentInformationsRepository = blockchainDeploymentInformationsRepository;
    this.connection = connection;
    this.gasProvider = gasProvider;
  }

  public DocumentContract getContractByAccount(Credentials accountCredentials) throws Exception {
    return blockchainDeploymentInformationsRepository
        .findContractByAccount(accountCredentials)
        .ifPresentOrElse(
            BlockchainDeploymentInformations::getContract,
            () ->
                blockchainDeploymentInformationsRepository
                    .save(deployContract(accountCredentials))
                    .getContract());
  }

  private BlockchainDeploymentInformations deployContract(Credentials account) throws Exception {
    DocumentContract contract = DocumentContract.deploy(connection, account, gasProvider).send();
    return new BlockchainDeploymentInformations(account, contract);
  }
}
