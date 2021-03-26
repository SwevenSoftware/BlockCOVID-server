package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.Token;
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
