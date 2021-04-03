package it.sweven.blockcovid.users.security;

import it.sweven.blockcovid.users.services.TokenService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer";
  private final TokenService tokenService;

  public TokenAuthenticationFilter(RequestMatcher requiresAuth, TokenService tokenService) {
    super(requiresAuth);
    this.tokenService = tokenService;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) {
    String token =
        Optional.ofNullable(request.getHeader(AUTHORIZATION))
            .map(v -> v.replace(BEARER, "").trim())
            .orElseThrow(() -> new BadCredentialsException("Missing authentication token."));
    String username = tokenService.getToken(token).getUsername();
    Authentication auth = new UsernamePasswordAuthenticationToken(username, token);
    return getAuthenticationManager().authenticate(auth);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }
}
