package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.BlockchainAccountNotFound;
import it.sweven.blockcovid.blockchain.exceptions.ContractNotDeployed;
import it.sweven.blockcovid.blockchain.exceptions.InvalidNetworkException;
import it.sweven.blockcovid.blockchain.services.BlockchainDeploymentInformationService;
import it.sweven.blockcovid.blockchain.services.BlockchainService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class BlockchainConfiguration {

  private final String contract, account, network;
  private final BlockchainDeploymentInformationService deploymentInformationService;
  private final BlockchainService blockchainService;

  @Autowired
  public BlockchainConfiguration(
      @Value("${it.sweven.blockcovid.blockchain.contract}") String contract,
      @Value("${it.sweven.blockcovid.blockchain.account}") String account,
      @Value("${it.sweven.blockcovid.blockchain.network}") String network,
      BlockchainDeploymentInformationService deploymentInformationService,
      BlockchainService blockchainService) {
    this.contract = contract;
    this.account = account;
    this.network = network;
    this.deploymentInformationService = deploymentInformationService;
    this.blockchainService = blockchainService;
  }

  @Bean
  public Web3j connection() {
    return Web3j.build(
        new HttpService(Optional.ofNullable(network).orElse("http://127.0.0.1:8545")));
  }

  @Bean
  public ContractGasProvider gasProvider() {
    return new DefaultGasProvider();
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
          account, network, blockchainService.deployContract(account).getContractAddress());
    }
  }
}
