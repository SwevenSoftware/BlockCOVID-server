package it.sweven.blockcovid.entities;

/* Java utilities */

import it.sweven.blockcovid.security.Authority;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  @Id private String username;
  private String password;
  private String token;
  private Set<Authority> authorities;
  private LocalDate expireDate;
  private LocalDate credentialsExpireDate;
  private boolean locked;
  private boolean enabled;

  public User(
      String username,
      String password,
      Set<Authority> authorities,
      LocalDate expireDate,
      LocalDate credentialsExpireDate) {
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.expireDate = expireDate;
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
  public int hashCode() {
    return Objects.hash(username, password, token);
  }

  @Override
  public String toString() {
    return "User{" + username + "}";
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
    return !LocalDate.now().isBefore(expireDate);
  }

  @Override
  public boolean isAccountNonLocked() {
    return locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !LocalDate.now().isBefore(credentialsExpireDate);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
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

  public String getToken() {
    return token;
  }

  public User setToken(String token) {
    this.token = token;
    return this;
  }
}
