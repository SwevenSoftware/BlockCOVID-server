package it.sweven.blockcovid.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.sweven.blockcovid.users.dto.CredentialsWithAuthorities;
import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import java.util.Set;
import javax.security.auth.login.CredentialException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserRegistrationServiceTest {

  private UserService userService;
  private UserRegistrationService registrationService;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    when(encoder.encode(anyString())).thenAnswer(i -> i.getArgument(0));
    registrationService = new UserRegistrationService(userService, encoder);
  }

  @Test
  void register_validCredentials() throws CredentialException {
    CredentialsWithAuthorities inputCredentials =
        new CredentialsWithAuthorities("inputUser", "inputPassword", Set.of(Authority.CLEANER));
    User expectedUser = new User("inputUser", "inputPassword", Set.of(Authority.CLEANER));
    when(userService.getByUsername("inputUser")).thenThrow(UsernameNotFoundException.class);
    when(userService.save(expectedUser)).thenReturn(expectedUser);
    assertEquals(expectedUser, registrationService.register(inputCredentials));
  }

  @Test
  void register_usernameAlreadyInUse_throwsCredentialException() {
    CredentialsWithAuthorities inputCredentials =
        new CredentialsWithAuthorities("inputUser", "inputPassword", Set.of(Authority.CLEANER));
    User user = new User("inputUser", "inputPassword", Set.of(Authority.CLEANER));
    when(userService.getByUsername("inputUser")).thenReturn(user);
    assertThrows(CredentialException.class, () -> registrationService.register(inputCredentials));
  }

  @Test
  void register_nullParameters_throwsBadCredentialsException() throws CredentialException {
    assertThrows(BadCredentialsException.class, () -> registrationService.register(null));
    assertThrows(
        BadCredentialsException.class,
        () ->
            registrationService.register(
                new CredentialsWithAuthorities(null, "validPassword", Set.of(Authority.ADMIN))));
    assertThrows(
        BadCredentialsException.class,
        () ->
            registrationService.register(
                new CredentialsWithAuthorities("validUsername", null, Set.of(Authority.USER))));
    assertThrows(
        BadCredentialsException.class,
        () ->
            registrationService.register(
                new CredentialsWithAuthorities("validUsername", "validPassword", null)));
  }
}
