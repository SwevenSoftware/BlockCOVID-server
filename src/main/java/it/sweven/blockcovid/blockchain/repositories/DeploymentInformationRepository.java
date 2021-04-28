package it.sweven.blockcovid.blockchain.repositories;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.web3j.crypto.Credentials;

@Repository
public interface DeploymentInformationRepository
    extends MongoRepository<DeploymentInformation, String> {
  Optional<DeploymentInformation> findByAccountAndNetwork(
      Credentials accountCredentials, String network);
}
