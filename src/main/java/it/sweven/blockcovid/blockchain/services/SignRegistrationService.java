package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
public class SignRegistrationService {
  private final DocumentContract contract;
  private final Logger logger = LoggerFactory.getLogger(SignRegistrationService.class);

  @Autowired
  public SignRegistrationService(DocumentContract contract) {
    this.contract = contract;
  }

  public TransactionReceipt registerString(String hash) throws Exception {
    TransactionReceipt receipt = contract.add(hash).send();
    BigInteger verify = contract.verify(hash).send();
    if (verify.compareTo(BigInteger.ZERO) <= 0) {
      throw new HashNotRegistered();
    }
    logger.info("Successfully added hash [" + hash + "] to the provided blockchain network");
    return receipt;
  }
}
