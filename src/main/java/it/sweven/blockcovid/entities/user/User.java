package it.sweven.blockcovid.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  @Id private String username;
  private String password;
  private Set<Authority> authorities;
  private LocalDateTime credentialsExpireDate;
  private boolean locked;
  private boolean enabled;

  public User() {}

  @PersistenceConstructor
  public User(
      String username,
      String password,
      Set<Authority> authorities,
      LocalDateTime credentialsExpireDate,
      boolean locked,
      boolean enabled) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.credentialsExpireDate = credentialsExpireDate;
    this.locked = locked;
    this.enabled = enabled;
  }

  public User(String username, String password, Set<Authority> authorities) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.credentialsExpireDate = LocalDateTime.now().plusMonths(3L);
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
        + ", authorities="
        + authorities
        + ", credentials_expDate="
        + credentialsExpireDate
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
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    if (credentialsExpireDate == null) return false;
    return LocalDateTime.now().isBefore(credentialsExpireDate);
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return enabled;
  }

  public User lock() {
    this.locked = true;
    return this;
  }

  public User unlock() {
    this.locked = false;
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

  @Override
  @JsonIgnore
  public String getPassword() {
    return this.password;
  }

  @JsonIgnore
  public User setPassword(String newPassword) {
    this.password = newPassword;
    return this;
  }
}
