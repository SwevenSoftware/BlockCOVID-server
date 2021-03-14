package it.sweven.blockcovid.configurations;

/* Spring imports */
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import it.sweven.blockcovid.security.TokenAuthenticationFilter;
import it.sweven.blockcovid.security.TokenAuthenticationProvider;
import it.sweven.blockcovid.services.TokenService;
import it.sweven.blockcovid.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
  private static final RequestMatcher PROTECTED_URLS = new AntPathRequestMatcher("/api/**");
  private static final RequestMatcher PUBLIC_URLS =
      new OrRequestMatcher(
          new NegatedRequestMatcher(PROTECTED_URLS),
          new AntPathRequestMatcher("/api/login"),
          new AntPathRequestMatcher("/**"),
          new AntPathRequestMatcher("/api/register"));

  private final TokenAuthenticationProvider authenticationProvider;
  private final UserService userService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final TokenAuthenticationProvider tokenProvider;

  @Autowired
  WebSecurityConfiguration(
      TokenAuthenticationProvider authenticationProvider,
      UserService userService,
      TokenService tokenService,
      PasswordEncoder passwordEncoder,
      TokenAuthenticationProvider tokenProvider) {
    this.authenticationProvider = authenticationProvider;
    this.userService = userService;
    this.tokenService = tokenService;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(this.authenticationProvider);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().requestMatchers(PUBLIC_URLS);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(STATELESS)
        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PROTECTED_URLS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .logout()
        .disable();
  }

  @Bean
  TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
    TokenAuthenticationFilter filter = new TokenAuthenticationFilter(PROTECTED_URLS, tokenService);
    filter.setAuthenticationManager(authenticationManager());
    filter.setAuthenticationSuccessHandler(successHandler());
    return filter;
  }

  @Bean
  SimpleUrlAuthenticationSuccessHandler successHandler() {
    SimpleUrlAuthenticationSuccessHandler successHandler =
        new SimpleUrlAuthenticationSuccessHandler();
    successHandler.setRedirectStrategy(new NoRedirectStrategy());
    return successHandler;
  }

  @Bean
  FilterRegistrationBean disableAutoRegistration(TokenAuthenticationFilter filter) {
    FilterRegistrationBean registration = new FilterRegistrationBean(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  AuthenticationEntryPoint forbiddenEntryPoint() {
    return new HttpStatusEntryPoint(FORBIDDEN);
  }
}
