package it.sweven.blockcovid.blockchain.services;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import it.sweven.blockcovid.blockchain.exceptions.HashNotRegistered;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.web3j.crypto.Credentials;
import org.web3j.documentcontract.DocumentContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

@Service
public class DeploymentService {

  private final Web3j connection;
  private final ContractGasProvider gasProvider;
  private final Logger logger = LoggerFactory.getLogger(DeploymentService.class);

  @Autowired
  public DeploymentService(Web3j connection, ContractGasProvider gasProvider) {
    this.connection = connection;
    this.gasProvider = gasProvider;
  }

  public TransactionReceipt registerReport(DocumentContract contract, Path reportFile)
      throws Exception {
    FileInputStream fileInputStream = new FileInputStream(reportFile.toFile());
    byte[] reportBytes = fileInputStream.readAllBytes();
    String reportHash = DigestUtils.md5DigestAsHex(reportBytes);
    TransactionReceipt receipt = contract.add(reportHash).send();
    BigInteger verify = contract.verify(reportHash).send();
    if (verify.compareTo(BigInteger.ZERO) <= 0) {
      throw new HashNotRegistered();
    }
    logger.info("Successfully added hash [" + reportHash + "] to the provided blockchain network");
    return receipt;
  }

  public DocumentContract loadContract(DeploymentInformation deploymentInformation)
      throws Exception {
    return DocumentContract.load(
        deploymentInformation.getContract(),
        connection,
        deploymentInformation.getAccount(),
        gasProvider);
  }

  public DocumentContract deployContract(Credentials account) throws Exception {
    return DocumentContract.deploy(connection, account, gasProvider).send();
  }
}
