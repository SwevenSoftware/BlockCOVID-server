package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import it.sweven.blockcovid.blockchain.exceptions.InvalidHash;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

  public LocalDateTime verifyHash(String hash) throws Exception, InvalidHash {
    BigInteger additionTimeBigInteger = contract.verify(hash).send();
    if (additionTimeBigInteger.equals(BigInteger.ZERO)) {
      logger.info("hash " + hash + " is not on the blockchain");
      throw new InvalidHash();
    } else {
      LocalDateTime additionTime =
          LocalDateTime.ofInstant(
              Instant.ofEpochMilli(additionTimeBigInteger.longValue()), ZoneId.systemDefault());
      logger.info(
          "hash " + hash + " is on the blockchain, and it was registered on " + additionTime);
      return additionTime;
    }
  }
}
