package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.Token;
import it.sweven.blockcovid.repositories.TokenRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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

  public void delete(String token) {
    tokenRepository.deleteById(token);
  }

  public List<Token> all() {
    return tokenRepository.findAll();
  }
}
