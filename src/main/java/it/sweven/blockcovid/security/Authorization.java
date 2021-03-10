package it.sweven.blockcovid.security;

import org.springframework.security.core.GrantedAuthority;

public enum Authorization implements GrantedAuthority {
  ADMIN,
  USER,
  CLEANER;

  @Override
  public String getAuthority() {
    return this.name();
  }
}
