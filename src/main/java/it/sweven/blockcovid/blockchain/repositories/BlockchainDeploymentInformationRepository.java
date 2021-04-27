package it.sweven.blockcovid.blockchain.repositories;

import it.sweven.blockcovid.blockchain.entities.BlockchainDeploymentInformation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.web3j.crypto.Credentials;

@Repository
public interface BlockchainDeploymentInformationRepository
    extends MongoRepository<BlockchainDeploymentInformation, String> {
  Optional<BlockchainDeploymentInformation> findByAccountAndNetwork(
      Credentials accountCredentials, String network);
}
