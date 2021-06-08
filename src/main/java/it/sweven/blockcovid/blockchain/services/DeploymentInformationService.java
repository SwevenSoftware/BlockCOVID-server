package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.repositories.DeploymentInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

@Service
public class DeploymentInformationService {
  private final DeploymentInformationRepository deploymentInformationRepository;

  @Autowired
  public DeploymentInformationService(
      DeploymentInformationRepository deploymentInformationRepository) {
    this.deploymentInformationRepository = deploymentInformationRepository;
  }

  public DeploymentInformation getByAccountAndNetwork(Credentials account, String network)
      throws ContractNotDeployed {
    return deploymentInformationRepository
        .findByAccountAndNetwork(account.getAddress(), network)
        .orElseThrow(ContractNotDeployed::new);
  }

  public DeploymentInformation save(DeploymentInformation deploymentInformation) {
    return deploymentInformationRepository.save(deploymentInformation);
  }
}
