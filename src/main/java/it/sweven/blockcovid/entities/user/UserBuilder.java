package it.sweven.blockcovid.entities.user;

import it.sweven.blockcovid.security.Authority;
import java.util.Set;

public class UserBuilder {
  private String username;
  private String password;
  private Set<Authority> authorities;

  public UserBuilder setUsername(String username) {
    this.username = username;
    return this;
  }

  public UserBuilder setPassword(String password) {
    this.password = password;
    return this;
  }

  public UserBuilder setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
    return this;
  }

  public User createUser() {
    return new User(username, password, authorities);
  }
}
