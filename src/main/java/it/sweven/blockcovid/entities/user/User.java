package it.sweven.blockcovid.entities.user;

/* Java utilities */

import it.sweven.blockcovid.security.Authority;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Set;

import net.minidev.json.annotate.JsonIgnore;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  @Id private String username;
  private @JsonIgnore String hashPassword;
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
    this.hashPassword = DigestUtils.sha256Hex(password);
    this.authorities = authorities;
    this.credentialsExpireDate = credentialsExpireDate;
    this.locked = false;
    this.enabled = true;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof User) {
      User other = (User) o;
      return username.equals(other.username) && hashPassword.equals(other.hashPassword);
    } else return false;
  }

  @Override
  public String toString() {
    return "User{ username="
        + username
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

  public boolean checkPassword(String password) {
    return hashPassword.equals(DigestUtils.sha256Hex(password));
  }

  @Override
  public String getPassword() {
    return this.hashPassword;
  }

  public User setPassword(String newPassword) {
    this.hashPassword = DigestUtils.sha256Hex(newPassword);
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
