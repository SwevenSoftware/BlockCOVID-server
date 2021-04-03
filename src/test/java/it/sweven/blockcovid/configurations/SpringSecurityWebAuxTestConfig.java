package it.sweven.blockcovid.configurations;

import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import java.util.Arrays;
import java.util.Set;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityWebAuxTestConfig {

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    User testUser = new User("user", "password", Set.of(Authority.USER));
    User testAdmin = new User("user", "password", Set.of(Authority.ADMIN));
    User testCleaner = new User("user", "password", Set.of(Authority.CLEANER));

    return new InMemoryUserDetailsManager(Arrays.asList(testUser, testAdmin, testCleaner));
  }
}
