package it.sweven.blockcovid.entities.user;

import java.time.LocalDateTime;

public class Token {
  private String token;
  private LocalDateTime expiryDate;

  public Token(String token, LocalDateTime expiryDate) {
    this.token = token;
    this.expiryDate = expiryDate;
  }

  public String getToken() {
    return token;
  }

  public Token setToken(String token) {
    this.token = token;
    return this;
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public boolean expired() {
    if(expiryDate == null)
      return true;
    return LocalDateTime.now().isAfter(expiryDate);
  }

  @Override
  public String toString() {
    return token;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Token) {
      Token o = (Token) other;
      return token.equals(o.token);
    } else return false;
  }

  public static Token fromString(String tokenString) {
    return new Token(tokenString, LocalDateTime.now());
  }
}
