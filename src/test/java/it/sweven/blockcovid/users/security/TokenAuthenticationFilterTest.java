package it.sweven.blockcovid.users.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.entities.Token;
import it.sweven.blockcovid.users.services.TokenService;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class TokenAuthenticationFilterTest {

  private @MockBean RequestMatcher matcher;
  private @MockBean AuthenticationManager manager;
  private TokenService service;
  private TokenAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    service = mock(TokenService.class);
    when(matcher.matches(any())).thenReturn(true);
    filter = new TokenAuthenticationFilter(matcher, service);
    filter.setAuthenticationManager(manager);
    when(manager.authenticate(any())).thenReturn(mock(Authentication.class));
  }

  @Test
  void attemptAuthentication_validAuthorization() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn("Bearer tokenId");
    Token token = mock(Token.class);
    AtomicBoolean correctToken = new AtomicBoolean(false);
    when(service.getToken(any()))
        .thenAnswer(
            invocation -> {
              if (invocation.getArgument(0, String.class).equals("tokenId")) correctToken.set(true);
              return token;
            });
    filter.attemptAuthentication(request, mock(HttpServletResponse.class));
    assertTrue(correctToken.get());
  }

  @Test
  void attemptAuthentication_invalidAuthorization_throwsBadCredentialsException() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn(null);
    assertThrows(
        BadCredentialsException.class,
        () -> filter.attemptAuthentication(request, mock(HttpServletResponse.class)));
  }

  @Test
  void successfulAuthentication_chainFilter() throws IOException, ServletException {
    FilterChain chain = mock(FilterChain.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    AtomicBoolean chainFiltered = new AtomicBoolean(false);
    doAnswer(
            invocation -> {
              chainFiltered.set(true);
              return null;
            })
        .when(chain)
        .doFilter(request, response);
    filter.successfulAuthentication(request, response, chain, mock(Authentication.class));
    assertTrue(chainFiltered.get());
  }
}
