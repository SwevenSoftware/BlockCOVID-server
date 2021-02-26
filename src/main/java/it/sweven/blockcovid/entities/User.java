package it.sweven.blockcovid.entities;

/* Java utilities */

import it.sweven.blockcovid.security.Authorization;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
@Getter
@Setter
@NoArgsConstructor
public class User {

  @Id private String username;
  private String password;
  private String token;
  private Set<Authorization> authorizations;
  private boolean expired;
  private boolean locked;

  public void setAdmin() {
    authorizations.add(Authorization.ADMIN);
  }

  public void setUser() {
    authorizations.add(Authorization.USER);
  }

  public void setCleaner() {
    authorizations.add(Authorization.CLEANER);
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
}
