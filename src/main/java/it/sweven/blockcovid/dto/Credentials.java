package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credentials {
  private final String username, password;

  public CredentialsWithAuthorities withAuthorities(Set<Authority> authorities) {
    return new CredentialsWithAuthorities(this.username, this.password, authorities);
  }
}
