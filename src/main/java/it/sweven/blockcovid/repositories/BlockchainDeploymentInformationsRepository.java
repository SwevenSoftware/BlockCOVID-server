package it.sweven.blockcovid.repositories;

import it.sweven.blockcovid.entities.BlockchainDeploymentInformations;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.web3j.crypto.Credentials;

public interface BlockchainDeploymentInformationsRepository
    extends MongoRepository<BlockchainDeploymentInformations, String> {
  Optional<BlockchainDeploymentInformations> findContractByAccount(Credentials accountCredentials);
}
