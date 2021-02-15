package it.sweven.blockcovid.entities;

/* Java utilities */
import java.util.Set;

/* Spring utilities */
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/* Our own imports */

@Document
public class User {

    private @Id String name;
    private String password;
    private Token token;
    private Set<Authorization> authorizations;

    public User(String name, String password) {
	this.name = name;
	this.password = password;
    }

    public boolean isUser() {
	return autorizations.contains(Authorization.USER);
    }
    
    public boolean isAdmin() {
	return autorizations.contains(Authorization.ADMIN);
    }
    
    public String getName() {
	return name;
    }

    public Set<Authorization> getAuthorizations() {
	return authorizations;
    }

    public String getToken() {
	return token.toString();
    }
    
    @Override
    public String toString() {
	return String.format("{'name': '%s', 'password': '%s'}",
			     name, password);
    }

    @Override
    public boolean equals(Object o) {
	if(instanceof User){
	    User other = (User)o;
	    return name.equals(other.name) && password.equals(other.password);
	} else return false;
    }
}
