package it.sweven.blockcovid;

import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import java.util.List;
import java.util.Set;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SpringSecurityAuthTestConfig {

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    User user = new User("user", "password", Set.of(Authority.USER)),
        admin = new User("admin", "password", Set.of(Authority.ADMIN)),
        cleaner = new User("cleaner", "password", Set.of(Authority.CLEANER));
    return new InMemoryUserDetailsManager(List.of(user, admin, cleaner));
  }
}
