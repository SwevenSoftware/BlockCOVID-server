package it.sweven.blockcovid.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
  @Bean
  public Logger blockchainLogger() {
    return LoggerFactory.getLogger("BlockchainService");
  }
}
