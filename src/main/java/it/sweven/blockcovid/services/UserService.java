package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.entities.user.UserBuilder;
import it.sweven.blockcovid.repositories.UserRepository;
import it.sweven.blockcovid.security.Authority;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

  public void updatePassword(User user, String newPassword) {
    save(user.setPassword(passwordEncoder.encode(newPassword)));
  }

  public void updateAuthorities(User user, Set<Authority> newAuthorities) {
    save(user.setAuthorities(newAuthorities));
  }

  @PostConstruct
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
