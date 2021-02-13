package it.sweven.blockcovid;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EntitaRepo extends MongoRepository<Entita, String> {
    public Entita findByUUID(String UUID);
    public List<Entita> findByVal3(String val3);
}
