package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.repositories.BlockchainDeploymentInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
public class BlockchainDeploymentInformationService {
  private final BlockchainDeploymentInformationRepository blockchainDeploymentInformationRepository;

  @Autowired
  public BlockchainDeploymentInformationService(
      BlockchainDeploymentInformationRepository blockchainDeploymentInformationRepository) {
    this.blockchainDeploymentInformationRepository = blockchainDeploymentInformationRepository;
  }

  public BlockchainDeploymentInformation getByAccountAndNetwork(Credentials account, String network)
      throws ContractNotDeployed {
    return blockchainDeploymentInformationRepository
        .findByAccountAndNetwork(account, network)
        .orElseThrow(ContractNotDeployed::new);
  }
}
