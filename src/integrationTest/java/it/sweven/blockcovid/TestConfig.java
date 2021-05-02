package it.sweven.blockcovid;

import static org.mockito.Mockito.mock;

import it.sweven.blockcovid.users.repositories.TokenRepository;
import it.sweven.blockcovid.users.repositories.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
  @Bean
  public UserRepository userRepository() {
    return mock(UserRepository.class);
  }

  @Bean
  public TokenRepository tokenRepository() {
    return mock(TokenRepository.class);
  }
}
