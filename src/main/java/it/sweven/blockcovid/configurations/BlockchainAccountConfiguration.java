package it.sweven.blockcovid.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;

@Configuration
public class BlockchainAccountConfiguration {
  private final String account;

  @Autowired
  public BlockchainAccountConfiguration(
      @Value("${it.sweven.blockcovid.blockchain.account}") String account) {
    this.account = account;
  }

  @Bean
  public Credentials account() {
    return Credentials.create(account);
  }
}
