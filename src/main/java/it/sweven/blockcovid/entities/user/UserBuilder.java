package it.sweven.blockcovid.entities.user;

import java.util.Collections;
import java.util.Set;
import org.springframework.security.authentication.BadCredentialsException;

public class UserBuilder {
  private String username;
  private String password;
  private Set<Authority> authorities = Collections.emptySet();

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

  public User createUser() throws BadCredentialsException {
    if (username != null && password != null) return new User(username, password, authorities);
    else throw new BadCredentialsException("username and password must both be setted");
  }
}
