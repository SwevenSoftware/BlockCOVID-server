package it.sweven.blockcovid.users.entities;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Token {
  private @Id String id;
  private LocalDateTime expiryDate;
  private String username;

  public Token(String id, LocalDateTime expiryDate, String username) {
    this.id = id;
    this.expiryDate = expiryDate;
    this.username = username;
  }

  public String getId() {
    return id;
  }

  public Token setId(String id) {
    this.id = id;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public boolean expired() {
    if (expiryDate == null) return true;
    return LocalDateTime.now().isAfter(expiryDate);
  }

  @Override
  public String toString() {
    return "Token{"
        + "id='"
        + id
        + '\''
        + ", expiryDate="
        + expiryDate
        + ", username='"
        + username
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Token) {
      Token o = (Token) other;
      return id.equals(o.id);
    } else return false;
  }
}
