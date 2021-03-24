package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credentials {
  @NotNull private final String username;
  @NotNull private final String password;

  public CredentialsWithAuthorities withAuthorities(Set<Authority> authorities) {
    return new CredentialsWithAuthorities(this.username, this.password, authorities);
  }
}
