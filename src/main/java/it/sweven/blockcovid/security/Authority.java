package it.sweven.blockcovid.security;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
  ADMIN,
  USER,
  CLEANER;

  @Override
  public String getAuthority() {
    return this.name();
  }
}
