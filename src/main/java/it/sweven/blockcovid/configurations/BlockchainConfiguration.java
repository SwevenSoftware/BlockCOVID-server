package it.sweven.blockcovid.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Configuration
public class BlockchainConfiguration {

  private final Environment env;

  private final String NETWORK = "NETWORK";
  private final String ACCOUNT = "ACCOUNT";
  private final String CONTRACT = "CONTRACT";

  private String effectiveContract;

  @Autowired
  BlockchainConfiguration(Environment env) {
    this.env = env;
    effectiveContract = env.getProperty(CONTRACT, "");
  }

  @Bean
  public Web3j connection() {
    return Web3j.build(new HttpService(env.getProperty(NETWORK, "http://127.0.0.1:8545")));
  }

  @Bean
  public ContractGasProvider gasProvider() {
    return new DefaultGasProvider();
  }

  @Bean
  public Credentials accountCredentials() {
    return Credentials.create(env.getRequiredProperty(ACCOUNT));
  }

  @Bean
  public Document document() throws Exception {
    if (effectiveContract.equals("")) {
      Document deployed = Document.deploy(connection(), accountCredentials(), gasProvider()).send();
      effectiveContract = deployed.getContractAddress();
      return deployed;
    } else {
      return Document.load(effectiveContract, connection(), accountCredentials(), gasProvider());
    }
  }
}
