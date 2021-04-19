package it.sweven.blockcovid.users.services;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  private final TokenRepository tokenRepository;

  @Autowired
  public TokenService(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  public Token getToken(String token) throws AuthenticationCredentialsNotFoundException {
    return tokenRepository
        .findById(token)
        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Token not found"));
  }

  public Token save(Token token) {
    return tokenRepository.save(token);
  }

  public Token delete(String token) throws AuthenticationException {
    return tokenRepository
        .deleteTokenById(token)
        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(token));
  }
}
