package it.sweven.blockcovid.blockchain;

import it.sweven.blockcovid.contracts.Document;
import it.sweven.blockcovid.repositories.ReservationRepository;
import it.sweven.blockcovid.repositories.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;

import java.time.LocalDate;

import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

@Component
public class EthereumRunner {

  @Autowired private final UserRepository userRepository;
  @Autowired private final ReservationRepository repository;

  private Web3j connection;
  
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  // abominio trovato online
  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  EthereumRunner(UserRepository userRepository) {
    this.userRepository = userRepository;
    connection = Web3j.build(new HttpService());
    /*
     * Se non è stato indicato un indirizzo del contratto, fare il deploy con
     * l'account corrente
     */
    if (EthereumConfiguration.CONTRACT_ADDRESS.equals("")) {
      try {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        Credentials credentials = Credentials.create(EthereumConfiguration.ACCOUNT);
        Document contract = Document.deploy(connection, credentials, contractGasProvider).send();
        EthereumConfiguration.CONTRACT_ADDRESS = new String(contract.getContractAddress());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  @Scheduled(cron = "* * * * * ?")
  public void run() throws NoSuchAlgorithmException {
    PdfReport newReport = new PdfReport(LocalDate.now(), LocalTime.now());
        String reportHash =
            newReport
                .addReservations(repository.findAll())
                .addHashPreviousReport("previousHash")
                .save()
                .hashFile();
    try {
      Credentials credentials = Credentials.create(EthereumConfiguration.ACCOUNT);
      ContractGasProvider contractGasProvider = new DefaultGasProvider();
      TransactionReceipt receipt =
          Document.load(
                  EthereumConfiguration.CONTRACT_ADDRESS,
                  connection,
                  credentials,
                  contractGasProvider)
              .add(reportHash)
              .send();
      System.out.println(receipt.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
