package it.sweven.blockcovid.blockchain;

import it.sweven.blockcovid.contracts.Document;
import it.sweven.blockcovid.documents.PdfReport;
import it.sweven.blockcovid.repositories.ReservationRepository;
import it.sweven.blockcovid.repositories.UserRepository;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
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

  @Autowired private final ReservationRepository reservationRepository;

  private Web3j connection;
  private EthereumConfiguration configuration;

  EthereumRunner(UserRepository userRepository, ReservationRepository reservationRepository) {
    this.userRepository = userRepository;
    this.reservationRepository = reservationRepository;

    configuration = new EthereumConfiguration();

    connection = Web3j.build(new HttpService(configuration.NETWORK));
    /*
     * Se non è stato indicato un indirizzo del contratto, fare il deploy con
     * l'account corrente
     */
    if (configuration.CONTRACT_ADDRESS.equals("")) {
      try {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        Credentials credentials = Credentials.create(configuration.ACCOUNT);
        Document contract = Document.deploy(connection, credentials, contractGasProvider).send();
        configuration.CONTRACT_ADDRESS = new String(contract.getContractAddress());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  @Scheduled(cron = "0 0 * * * ?")
  public void run() throws NoSuchAlgorithmException, IOException {
    PdfReport newReport = new PdfReport(LocalDate.now(), LocalTime.now());
    String reportHash =
        newReport
            .addReservations(reservationRepository.findAll())
            .addHashPreviousReport("previousHash")
            .save()
            .hashFile();
    try {
      Credentials credentials = Credentials.create(configuration.ACCOUNT);
      ContractGasProvider contractGasProvider = new DefaultGasProvider();
      TransactionReceipt receipt =
          Document.load(
                  configuration.CONTRACT_ADDRESS, connection, credentials, contractGasProvider)
              .add(reportHash)
              .send();
      System.out.println(receipt.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
