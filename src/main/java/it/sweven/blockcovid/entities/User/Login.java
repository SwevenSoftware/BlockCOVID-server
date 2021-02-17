package it.sweven.blockcovid.entities.User;

import it.sweven.blockcovid.repositories.UserRepository;

public class Login {    
    public static User fromLoginForm(LoginForm login, UserRepository repo) {
	return repo.findByLogin(login);
    }

    public static User fromToken(String token, UserRepository repo) {
	return repo.findByToken(token);
    }
}
