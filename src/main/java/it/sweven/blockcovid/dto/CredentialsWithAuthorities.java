package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CredentialsWithAuthorities {
  @NotNull
  @Size(min = 5)
  private String username;

  @NotNull
  @Size(min = 8)
  private String password;

  @NotNull
  @Size(min = 1)
  private Set<Authority> authorities;
}
