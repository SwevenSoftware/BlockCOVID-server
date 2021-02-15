package it.sweven.blockcovid.entities;

/* Java utilities */
import java.util.Set;
import java.util.Date;

/* Spring utilities */
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/* Our own imports */

@Document
public class User {

    private LoginForm login;
    private @Id Token token;
    private Set<Authorization> authorizations;

    public User(String name, String password) {
	this.login = new LoginForm(name, password);
	this.token = new Token(name + password);
    }

    public boolean isUser() {
	return authorizations.contains(Authorization.USER);
    }
    
    public boolean isAdmin() {
	return authorizations.contains(Authorization.ADMIN);
    }

    public boolean isCleaner() {
	return authorizations.contains(Authorization.CLEANER);
    }
    
    public String getName() {
	return login.getName();
    }

    public Set<Authorization> getAuthorizations() {
	return authorizations;
    }

    public String getToken() {
	return token.toString();
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
	    return login.equals(other.login);
	} else return false;
    }
}
