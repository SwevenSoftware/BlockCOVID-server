package it.sweven.blockcovid.services;

import it.sweven.blockcovid.exceptions.HashNotRegistered;
import java.io.FileInputStream;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.web3j.crypto.Credentials;
import org.web3j.document.Document;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class BlockchainService {

  private final Document contract;
  private final Credentials account;
  private final ContractGasProvider gasProvider;
  private final Web3j connection;
  private final Logger log;

  @Autowired
  BlockchainService(
      Document contract,
      Credentials account,
      ContractGasProvider gasProvider,
      Web3j connection,
      Logger logger) {
    this.contract = contract;
    this.account = account;
    this.gasProvider = gasProvider;
    this.connection = connection;
    log = logger;
  }

  public TransactionReceipt registerReport(FileInputStream reportFile) throws Exception {
    byte[] reportBytes = reportFile.readAllBytes();
    String reportHash = DigestUtils.md5DigestAsHex(reportBytes);
    TransactionReceipt receipt = contract.add(reportHash).send();
    BigInteger verify = contract.verify(reportHash).send();
    if (verify.compareTo(BigInteger.ZERO) <= 0) {
      throw new HashNotRegistered();
    }
    log.info("Successfully added hash [" + reportHash + "] to the provided blockchain network");
    return receipt;
  }
}
