package it.sweven.blockcovid.blockchain.repositories;

import it.sweven.blockcovid.blockchain.entities.ReportInformation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportInformationRepository extends MongoRepository<ReportInformation, String> {
  Optional<ReportInformation> findByName(String name);
}
