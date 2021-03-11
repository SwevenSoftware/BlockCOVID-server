package it.sweven.blockcovid.entities.user;

/* Java utilities */

import it.sweven.blockcovid.security.Authority;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  @Id private String username;
  private String password;
  private Token token;
  private Set<Authority> authorities;
  private LocalDateTime credentialsExpireDate;
  private boolean locked;
  private boolean enabled;

  public User(
      String username,
      String password,
      Set<Authority> authorities,
      LocalDateTime credentialsExpireDate) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.credentialsExpireDate = credentialsExpireDate;
    this.locked = false;
    this.enabled = true;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof User) {
      User other = (User) o;
      return username.equals(other.username) && password.equals(other.password);
    } else return false;
  }

  @Override
  public String toString() {
    return "User{ username="
        + username
        + ", password="
        + password
        + ", token="
        + token.toString()
        + ", authorities="
        + authorities.toString()
        + ", credentials_expDate="
        + credentialsExpireDate.toString()
        + ", locked="
        + locked
        + ", enabled="
        + enabled
        + "}";
  }

  @Override
  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public User setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
    return this;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !LocalDateTime.now().isBefore(credentialsExpireDate);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public boolean isTokenNonExpired() {
    return token.expired();
  }

  public User lock() {
    this.locked = true;
    return this;
  }

  public User unlock() {
    this.locked = true;
    return this;
  }

  public User disable() {
    this.enabled = false;
    return this;
  }

  public User enable() {
    this.enabled = true;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public User setUsername(String newUsername) {
    this.username = newUsername;
    return this;
  }

  public String getPassword() {
    return password;
  }

  public User setPassword(String newPassword) {
    this.password = newPassword;
    return this;
  }

  public Token getToken() {
    return token;
  }

  public User setToken(Token token) {
    this.token = token;
    return this;
  }
}
