package it.sweven.blockcovid.blockchain;

import java.util.Map;

class EthereumConfiguration {
  public final String NETWORK;
  public final String ACCOUNT;
  public String CONTRACT_ADDRESS;

  EthereumConfiguration() {
    Map<String, String> env = System.getenv();
    NETWORK = new String(env.getOrDefault("NETWORK", "http://127.0.0.1:8545"));
    ACCOUNT = new String(env.getOrDefault("ACCOUNT", ""));
    CONTRACT_ADDRESS = new String(env.getOrDefault("CONTRACT_ADDRESS", ""));
  }
}
