package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import java.io.FileInputStream;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class BlockchainService {

  private final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

  public TransactionReceipt registerReport(DocumentContract contract, FileInputStream reportFile)
      throws Exception {
    byte[] reportBytes = reportFile.readAllBytes();
    String reportHash = DigestUtils.md5DigestAsHex(reportBytes);
    TransactionReceipt receipt = contract.add(reportHash).send();
    BigInteger verify = contract.verify(reportHash).send();
    if (verify.compareTo(BigInteger.ZERO) <= 0) {
      throw new HashNotRegistered();
    }
    logger.info("Successfully added hash [" + reportHash + "] to the provided blockchain network");
    return receipt;
  }
}
