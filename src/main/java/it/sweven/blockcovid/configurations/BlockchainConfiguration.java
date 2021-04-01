package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.exceptions.BlockchainAccountNotFound;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class BlockchainConfiguration {

  private String contract;

  public BlockchainConfiguration(
      @Value("it.sweven.blockcovid.blockchain.contract") String contract) {
    this.contract = contract;
  }

  @Bean
  public Web3j connection(@Value("it.sweven.blockcovid.blockchain.network") String network) {
    return Web3j.build(
        new HttpService(Optional.ofNullable(network).orElse("http://127.0.0.1:8545")));
  }

  @Bean
  public ContractGasProvider gasProvider() {
    return new DefaultGasProvider();
  }

  @Bean
  public Credentials accountCredentials(
      @Value("it.sweven.blockcovid.blockchain.account") String account)
      throws BlockchainAccountNotFound {
    return Credentials.create(
        Optional.ofNullable(account).orElseThrow(BlockchainAccountNotFound::new));
  }

  @Bean
  public Document document(
      @Value("it.sweven.blockcovid.blockchain.network") String network,
      @Value("it.sweven.blockcovid.blockchain.account") String account)
      throws Exception, BlockchainAccountNotFound {
    if (contract == null) {
      Document deployed =
          Document.deploy(connection(network), accountCredentials(account), gasProvider()).send();
      contract = deployed.getContractAddress();
      return deployed;
    } else {
      return Document.load(
          contract, connection(network), accountCredentials(account), gasProvider());
    }
  }
}
