package it.sweven.blockcovid.entities.User;

/* Java utilities */
import java.util.Set;
import java.util.Date;

/* Spring utilities */
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

/* Our own imports */

@Document
public class User implements UserDetails {

    @Id
    private String username;
    private String password;
    private String token;
    private Set<Authorization> authorizations;
    private boolean expired;
    private boolean locked;

    public User(String username, String password) {
	this.username = username;
	this.password = password;
	this.token = Token.generateToken(username + password);
	this.expired = false;
	this.locked = false;
    }

    @Override
    public String getUsername() {
	return username;
    }

    @Override
    public String getPassword() {
	return password;
    }

    @Override
    public Set<Authorization> getAuthorities() {
	return authorizations;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
	return !expired && !locked;
    }

    public String getToken() {
	return token;
    }

    public LoginForm getLogin() {
	return new LoginForm(username, password);
    }

    public void setAdmin() {
	authorizations.add(Authorization.ADMIN);
    }

    public void setUser() {
	authorizations.add(Authorization.USER);
    }

    public void setCleaner() {
	authorizations.add(Authorization.CLEANER);
    }

    @Override
    public boolean equals(Object o) {
	if(o instanceof User){
	    User other = (User)o;
	    return username.equals(other.username) && password.equals(other.password);
	} else return false;
    }
}
