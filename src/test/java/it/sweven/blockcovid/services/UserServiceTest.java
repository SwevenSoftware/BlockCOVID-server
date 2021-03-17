package it.sweven.blockcovid.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import it.sweven.blockcovid.entities.user.Authority;
import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.repositories.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

  private UserRepository repository;
  private PasswordEncoder encoder;
  private UserService service;

  @BeforeEach
  void setup() {
    repository = mock(UserRepository.class);
    encoder = mock(PasswordEncoder.class);
    when(encoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    service = new UserService(repository, encoder);
  }

  @Test
  void save_checkReturnUser() {
    User expectedUser = new User("user", "password", Set.of(Authority.USER, Authority.ADMIN));
    when(repository.save(expectedUser)).thenReturn(expectedUser);
    assertEquals(expectedUser, service.save(expectedUser));
  }

  @Test
  void save_checkRepositorySaveIsCalled() {
    AtomicBoolean userSaved = new AtomicBoolean(false);
    when(repository.save(any()))
        .thenAnswer(
            invocation -> {
              userSaved.set(true);
              return invocation.getArgument(0);
            });
    service.save(new User("user", "password", Set.of(Authority.USER, Authority.ADMIN)));
    assertTrue(userSaved.get());
  }

  @Test
  void getByUsername_validUsername() {
    User expectedUser = new User("user", "password", Set.of(Authority.CLEANER));
    when(repository.findByUsername("user")).thenReturn(Optional.of(expectedUser));
    assertEquals(expectedUser, service.getByUsername("user"));
  }

  @Test
  void getByUsername_usernameNotFound_throwsUsernameNotFoundException() {
    assertThrows(UsernameNotFoundException.class, () -> service.getByUsername("invalidUsername"));
  }

  @Test
  void findAllUsers() {
    List<User> expectedList =
        List.of(
            new User("user1", "password1", Set.of(Authority.CLEANER)),
            new User("user2", "password2", Set.of(Authority.USER, Authority.ADMIN)),
            new User("user3", "password3", Collections.emptySet()));
    when(repository.findAll()).thenReturn(expectedList);
    assertEquals(expectedList, service.getAllUsers());
  }

  @Test
  void loadUserByUsername_validUsername() {
    User expectedUser = new User("user", "password", Collections.emptySet());
    when(repository.findByUsername("user")).thenReturn(Optional.of(expectedUser));
    assertEquals(expectedUser, service.loadUserByUsername("user"));
  }

  @Test
  void loadUserByUsername_usernameNotFound_throwsUsernameNotFoundException() {
    assertThrows(UsernameNotFoundException.class, () -> service.getByUsername("invalidUsername"));
  }

  @Test
  void updatePassword_checkRepositorySaveIsCalled() {
    User user = new User("user", "password", Set.of(Authority.USER));
    when(encoder.matches("oldPassword", user.getPassword())).thenReturn(true);
    AtomicBoolean passwordUpdated = new AtomicBoolean(false);
    when(repository.save(user))
        .thenAnswer(
            invocation -> {
              if (invocation.getArgument(0, User.class).getPassword().equals("newPassword"))
                passwordUpdated.set(true);
              return invocation.getArgument(0);
            });
    service.updatePassword(user, "oldPassword", "newPassword");
    assertTrue(passwordUpdated.get());
  }

  @Test
  void updatePassword_invalidOldPassword_throwsBadCredentialsException() {
    assertThrows(
        BadCredentialsException.class,
        () ->
            service.updatePassword(
                new User("user", "password", Collections.emptySet()),
                "oldPassword",
                "newPassword"));
  }

  @Test
  void setPassword_checkRepositorySaveIsCalled() {
    User user = new User("user", "password", Set.of(Authority.ADMIN, Authority.CLEANER));
    AtomicBoolean passwordSet = new AtomicBoolean(false);
    when(repository.save(user))
        .thenAnswer(
            invocation -> {
              if (invocation.getArgument(0, User.class).getPassword().equals("newPassword"))
                passwordSet.set(true);
              return invocation.getArgument(0);
            });
    service.setPassword(user, "newPassword");
    assertTrue(passwordSet.get());
  }

  @Test
  void updateAuthorities_checkRepositorySaveIsCalled() {
    Set<Authority> expectedAuthorities = Set.of(Authority.USER, Authority.ADMIN);
    User user = new User("user", "password", Set.of(Authority.CLEANER));
    AtomicBoolean authoritiesUpdated = new AtomicBoolean(false);
    when(repository.save(user))
        .thenAnswer(
            invocation -> {
              if (invocation
                  .getArgument(0, User.class)
                  .getAuthorities()
                  .equals(expectedAuthorities)) authoritiesUpdated.set(true);
              return invocation.getArgument(0);
            });
    service.updateAuthorities(user, expectedAuthorities);
    assertTrue(authoritiesUpdated.get());
  }

  @Test
  void deleteByUsername_validUsername_checkReturnUser() {
    User expectedUser = new User("user", "password", Set.of(Authority.CLEANER, Authority.ADMIN));
    when(repository.deleteUserByUsername("user")).thenReturn(Optional.of(expectedUser));
    assertEquals(expectedUser, service.deleteByUsername("user"));
  }

  @Test
  void deleteByUsername_validUsername_checkRepositoryDeleteIsCalled() {
    User user = new User("user", "password", Set.of(Authority.CLEANER, Authority.ADMIN));
    AtomicBoolean userDeleted = new AtomicBoolean(false);
    when(repository.deleteUserByUsername("user"))
        .thenAnswer(
            invocation -> {
              userDeleted.set(true);
              return Optional.of(user);
            });
    service.deleteByUsername("user");
    assertTrue(userDeleted.get());
  }

  @Test
  void deleteByUsername_invalidUsername_throwsUsernameNotFoundException() {
    when(repository.deleteUserByUsername("user")).thenReturn(Optional.empty());
    assertThrows(UsernameNotFoundException.class, () -> service.deleteByUsername("user"));
  }
}
