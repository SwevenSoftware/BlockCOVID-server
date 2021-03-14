package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.user.User;
import it.sweven.blockcovid.security.Authority;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserUpdateService(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  public void updatePassword(User user, String newPassword) {
    userService.save(user.setPassword(passwordEncoder.encode(newPassword)));
  }

  public void updateAuthorities(User user, Set<Authority> newAuthorities) {
    userService.save(user.setAuthorities(newAuthorities));
  }
}
