package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.blockchain.exceptions.BlockchainAccountNotFound;
import java.util.Optional;
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

  public BlockchainConfiguration(
      @Value("${it.sweven.blockcovid.blockchain.contract}") String contract,
      @Value("${it.sweven.blockcovid.blockchain.account}") String account,
      @Value("${it.sweven.blockcovid.blockchain.network}") String network) {
    this.contract = contract;
    this.account = account;
    this.network = network;
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
  public Credentials accountCredentials() throws BlockchainAccountNotFound {
    return Credentials.create(
        Optional.ofNullable(account).orElseThrow(BlockchainAccountNotFound::new));
  }
}
