package it.sweven.blockcovid.configurations;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Collection;
import java.util.Collections;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "it.sweven.blockcovid.repositories")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

  @Override
  protected String getDatabaseName() {
    return "blockcovid-test";
  }

  @Override
  public MongoClient mongoClient() {
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder().build();
    return MongoClients.create(mongoClientSettings);
  }

  @Override
  public Collection getMappingBasePackages() {
    return Collections.singleton("it.sweven.blockcovid.entities");
  }
}
