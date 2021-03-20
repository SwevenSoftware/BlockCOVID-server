package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.Token;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TokenWithAuthorities {
  private Token token;
  private Set<Authority> authorities;
}
