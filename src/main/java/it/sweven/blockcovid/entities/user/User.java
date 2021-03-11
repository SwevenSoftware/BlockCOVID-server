package it.sweven.blockcovid.entities.user;

/* Java utilities */

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.sweven.blockcovid.security.Authority;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class User implements UserDetails {

  @Id private String username;
  private @Transient @JsonIgnore String password;
  private String hashPassword;
  private @JsonIgnore Token token;
  private Set<Authority> authorities;
  private LocalDateTime credentialsExpireDate;
  private boolean locked;
  private boolean enabled;

  private @Transient PasswordEncoder passwordEncoder =
      PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public User() {}

  @PersistenceConstructor
  public User(
      String username,
      String hashPassword,
      Token token,
      Set<Authority> authorities,
      LocalDateTime credentialsExpireDate,
      boolean locked,
      boolean enabled) {
    this.username = username;
    this.hashPassword = hashPassword;
    this.token = token;
    this.authorities = authorities;
    this.credentialsExpireDate = credentialsExpireDate;
    this.locked = locked;
    this.enabled = enabled;
  }

  public User(String username, String password, Set<Authority> authorities) {
    this.username = username;
    this.password = password;
    this.hashPassword = passwordEncoder.encode(password);
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
        + ", token="
        + token
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

  /**
   * Check if password matches the stored one
   *
   * @param password Raw password to check
   * @return true if given password corresponds with the one stored
   */
  public boolean checkPassword(String password) {
    return passwordEncoder.matches(password, this.hashPassword);
  }

  @Override
  @JsonIgnore
  /**
   * Password is a transient value, thus a non-null value will be returned only if the password has
   * been just set in this object. Calling this method after retrieving the document from the
   * database, without first setting the password, will result in a null value.
   *
   * @return Plain text password or null
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Change this object password, both a plain text password and an hashed one will be saved, but
   * only the latter will be saved in the database, while the former will be available through
   * getPassword() until this object is destroyed
   *
   * @param newPassword New raw password to set
   * @return this object modified
   */
  public User setPassword(String newPassword) {
    this.password = newPassword;
    this.hashPassword = passwordEncoder.encode(newPassword);
    return this;
  }

  @JsonIgnore
  public String getHashPassword() {
    return this.hashPassword;
  }

  @JsonIgnore
  /**
   * Setter used by the database repository, please use setPassword() to set a new password. Do not
   * change the hashPassword value directly.
   */
  public User setHashPassword(String hashPassword) {
    this.hashPassword = hashPassword;
    return this;
  }

  public Token getToken() {
    return token;
  }

  public User setToken(Token token) {
    this.token = token;
    return this;
  }

  protected User setPasswordEncoder(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    return this;
  }
}
