package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.BlockchainAccountNotFound;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.exceptions.InvalidNetworkException;
import it.sweven.blockcovid.blockchain.services.DeploymentInformationService;
import it.sweven.blockcovid.blockchain.services.DeploymentService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class BlockchainConfiguration {

  private final String contract, account, network;
  private final DeploymentInformationService deploymentInformationService;
  private final DeploymentService deploymentService;

  @Autowired
  public BlockchainConfiguration(
      @Value("${it.sweven.blockcovid.blockchain.contract}") String contract,
      @Value("${it.sweven.blockcovid.blockchain.account}") String account,
      @Value("${it.sweven.blockcovid.blockchain.network}") String network,
      DeploymentInformationService deploymentInformationService,
      DeploymentService deploymentService) {
    this.contract = contract;
    this.account = account;
    this.network = network;
    this.deploymentInformationService = deploymentInformationService;
    this.deploymentService = deploymentService;
  }

  @Bean
  public BlockchainDeploymentInformation deploymentInformation()
      throws BlockchainAccountNotFound, InvalidNetworkException, Exception {
    Credentials account =
        Credentials.create(
            Optional.ofNullable(this.account).orElseThrow(BlockchainAccountNotFound::new));
    if (network == null || network.equals("")) throw new InvalidNetworkException();
    try {
      return deploymentInformationService.getByAccountAndNetwork(account, network);
    } catch (ContractNotDeployed contractNotDeployed) {
      return new BlockchainDeploymentInformation(
          account, network, deploymentService.deployContract(account).getContractAddress());
    }
  }
}
