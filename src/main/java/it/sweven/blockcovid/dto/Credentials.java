package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credentials {
  private final String username;

  private final String password;

  public CredentialsWithAuthorities withAuthorities(
      @NotNull @Size(min = 1) Set<Authority> authorities) {
    return new CredentialsWithAuthorities(this.username, this.password, authorities);
  }
}
