package it.sweven.blockcovid.blockchain.repositories;

import it.sweven.blockcovid.blockchain.entities.DeploymentInformation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeploymentInformationRepository
    extends MongoRepository<DeploymentInformation, String> {
  Optional<DeploymentInformation> findByAccountAndNetwork(String account, String network);
}
