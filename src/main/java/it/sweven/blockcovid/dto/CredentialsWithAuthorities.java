package it.sweven.blockcovid.dto;

import it.sweven.blockcovid.entities.user.Authority;
import java.util.Set;

public class CredentialsWithAuthorities {
  private String username, password;
  private Set<Authority> authorities;

  public CredentialsWithAuthorities(String username, String password, Set<Authority> authorities) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }
}
