package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Credentials {
  @NotNull
  @Size(min = 4, max = 16)
  @Pattern(regexp = "^[a-zA-Z0-9]*$")
  private final String username;

  @NotNull
  @Size(min = 8)
  private final String password;

  public CredentialsWithAuthorities withAuthorities(
      @NotNull @Size(min = 1) Set<Authority> authorities) {
    return new CredentialsWithAuthorities(this.username, this.password, authorities);
  }
}
