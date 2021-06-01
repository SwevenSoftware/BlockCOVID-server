package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.BlockchainAccountNotFound;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.exceptions.InvalidNetworkException;
import it.sweven.blockcovid.blockchain.services.DeploymentInformationService;
import it.sweven.blockcovid.blockchain.services.DeploymentService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
    if (network == null || network.equals("")) throw new InvalidNetworkException();
    if (contract == null || contract.equals("")) {
      try {
        logger.info("Checking for already deployed contract on the network");
        return deploymentInformationService.getByAccountAndNetwork(account(), network);
      } catch (ContractNotDeployed contractNotDeployed) {
        logger.info("Contract not deployed yet, deploying a new one");
        return deploymentInformationService.save(
            new DeploymentInformation(
                account(),
                deploymentService.deployContract(account()).getContractAddress(),
                network));
      }
    } else return new DeploymentInformation(account(), contract, network);
  }

  @Bean
  @Profile("ganache")
  public DeploymentInformation ganacheDeploymentInformation()
      throws InvalidNetworkException, BlockchainAccountNotFound, Exception {
    if (network == null || network.equals("")) throw new InvalidNetworkException();
    logger.info("Deploying on network " + network + " trough account " + account().getAddress());
    return new DeploymentInformation(
        account(), deploymentService.deployContract(account()).getContractAddress(), network);
  }

  @Bean
  public DocumentContract contract(DeploymentInformation information) throws Exception {
    return deploymentService.loadContract(information);
  }
}
