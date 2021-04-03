package it.sweven.blockcovid.users.entities;

import java.util.Collections;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.security.authentication.BadCredentialsException;

public class UserBuilder {
  private String username;
  private String password;
  private Set<Authority> authorities = Collections.emptySet();

  public UserBuilder setUsername(
      @NotNull @Size(min = 4, max = 16) @Pattern(regexp = "^[a-zA-Z0-9]*$") String username) {
    this.username = username;
    return this;
  }

  public UserBuilder setPassword(String password) {
    this.password = password;
    return this;
  }

  public UserBuilder setAuthorities(@Size(min = 1) Set<Authority> authorities)
      throws IllegalArgumentException {
    this.authorities = authorities;
    return this;
  }

  public User createUser() throws BadCredentialsException {
    if (username != null && password != null) return new User(username, password, authorities);
    else throw new BadCredentialsException("username and password must both be setted");
  }
}
