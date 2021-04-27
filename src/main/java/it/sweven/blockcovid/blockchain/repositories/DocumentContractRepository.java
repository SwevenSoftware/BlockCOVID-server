package it.sweven.blockcovid.blockchain.repositories;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.web3j.crypto.Credentials;

public interface DocumentContractRepository
    extends MongoRepository<BlockchainDeploymentInformation, String> {
  Optional<BlockchainDeploymentInformation> findByAccountAndNetwork(
      Credentials accountCredentials, String network);
}
