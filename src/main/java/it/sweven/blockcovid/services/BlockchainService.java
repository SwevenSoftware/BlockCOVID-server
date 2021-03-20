package it.sweven.blockcovid.services;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class BlockchainService {

  Document contract;
  Credentials account;
  ContractGasProvider gasProvider;
  Web3j connection;

  Logger log = LoggerFactory.getLogger(BlockchainService.class);

  @Autowired
  BlockchainService(
      Document contract, Credentials account, ContractGasProvider gasProvider, Web3j connection) {
    this.contract = contract;
    this.account = account;
    this.gasProvider = gasProvider;
    this.connection = connection;
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void run() throws Exception {
    // for now implemented through simple string, when report generation will be completed this will
    // be the hash of the generated report
    String reportHash = LocalDateTime.now().toString();
    TransactionReceipt receipt = contract.add(reportHash).send();
    log.info("Successfully added hash [" + reportHash + "] to the provided blockchain network");
  }
}
