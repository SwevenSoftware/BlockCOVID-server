package it.sweven.blockcovid.users.dto;

import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.Token;
import java.util.Set;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TokenWithAuthorities {
  private final Token token;
  private @Size(min = 1) final Set<Authority> authorities;
}
