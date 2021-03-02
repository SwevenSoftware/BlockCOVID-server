package it.sweven.blockcovid.services;

import it.sweven.blockcovid.entities.User;
import it.sweven.blockcovid.repositories.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private UserRepository userRepository;

  @Autowired
  UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  public Optional<User> getByToken(String token) {
    return userRepository.findByToken(token);
  }

  public Optional<User> getByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
