package it.sweven.blockcovid.users.services;

import it.sweven.blockcovid.users.entities.Authority;
import it.sweven.blockcovid.users.entities.User;
import it.sweven.blockcovid.users.entities.UserBuilder;
import it.sweven.blockcovid.users.repositories.UserRepository;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    initDB();
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public User getByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("username" + username + "not found"));
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return getByUsername(username);
  }

  public void updatePassword(User user, String oldPassword, String newPassword)
      throws BadCredentialsException {
    if (passwordEncoder.matches(oldPassword, user.getPassword()))
      save(user.setPassword(passwordEncoder.encode(newPassword)));
    else throw new BadCredentialsException("Old password does not match");
  }

  public void setPassword(User user, String newPassword) {
    save(user.setPassword(passwordEncoder.encode(newPassword)));
  }

  public void updateAuthorities(User user, Set<Authority> newAuthorities) {
    save(user.setAuthorities(newAuthorities));
  }

  private void initDB() {
    if (userRepository.findAll().isEmpty()) {
      UserBuilder builder = new UserBuilder();
      userRepository.save(
          builder
              .setUsername("admin")
              .setPassword(passwordEncoder.encode("password"))
              .setAuthorities(Set.of(Authority.ADMIN))
              .createUser());
    }
  }

  public User deleteByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .deleteUserByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("username " + username + " not found"));
  }
}
