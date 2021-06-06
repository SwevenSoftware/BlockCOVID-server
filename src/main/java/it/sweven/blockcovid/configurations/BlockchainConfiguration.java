package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
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
import org.springframework.context.annotation.Profile;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;

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
  public Credentials account() throws BlockchainAccountNotFound {
    return Credentials.create(
        Optional.ofNullable(this.account).orElseThrow(BlockchainAccountNotFound::new));
  }

  @Bean
  @Profile("!ganache")
  public DeploymentInformation deploymentInformation()
      throws BlockchainAccountNotFound, InvalidNetworkException, Exception {
    Credentials account = account();
    if (network == null || network.equals("")) throw new InvalidNetworkException();
    if (contract == null || contract.equals("")) {
      try {
        return deploymentInformationService.getByAccountAndNetwork(account, network);
      } catch (ContractNotDeployed contractNotDeployed) {
        return deploymentInformationService.save(
            new DeploymentInformation(
                account.getAddress(),
                network,
                deploymentService.deployContract(account).getContractAddress()));
      }
    } else return new DeploymentInformation(account.getAddress(), contract, network);
  }

  @Bean
  @Profile("ganache")
  public DeploymentInformation ganacheDeploymentInformation()
      throws InvalidNetworkException, BlockchainAccountNotFound, Exception {
    if (network == null || network.equals("")) throw new InvalidNetworkException();
    return new DeploymentInformation(
        account().getAddress(),
        deploymentService.deployContract(account()).getContractAddress(),
        network);
  }

  @Bean
  public DocumentContract contract(DeploymentInformation information) throws Exception {
    return deploymentService.loadContract(information);
  }
}
